/*
 * Copyright (C) 2015-2017 Level Software LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.levelsoftware.carculator.sync.vehicle;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.levelsoftware.carculator.BuildConfig;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;
import io.levelsoftware.carculator.sync.BaseIntentService;
import io.levelsoftware.carculator.util.NetworkManager;
import io.levelsoftware.carculator.util.NetworkUtils;
import io.levelsoftware.carculator.util.PreferenceUtils;
import io.levelsoftware.carculator.util.ProviderUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


public class VehicleIntentService extends BaseIntentService {

    private static final String CONTENT_URL = "https://uda-capstone.firebaseapp.com/vehicles.json";
    private static final int MINIMUM_UPDATE_INTERVAL = 1000 * 60 * 2 ; // 2 minutes

    private static final String SERVICE_NAME = "VehicleService";
    private static boolean isRunning;

    public VehicleIntentService() {
        super(SERVICE_NAME);
    }

    public static synchronized boolean isRunning() {
        return isRunning;
    }

    public static void start(Context context) {
        context.startService(new Intent(context, VehicleIntentService.class));
    }

    protected static synchronized void setIsRunning(boolean isRunning) {
        VehicleIntentService.isRunning = isRunning;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("Running vehicle intent service...");
        setIsRunning(true);

        if(dataNeedsUpdate(getString(R.string.pref_vehicle_last_update), MINIMUM_UPDATE_INTERVAL)) {
            updateData();
        } else {
            sendStatusBroadcast(STATUS_ERROR_DATA_CURRENT, "Data up to date");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("Vehicle intent service destroyed");
        setIsRunning(false);
    }

    private void updateData() {
        if(NetworkUtils.networkIsAvailable(this)) {
            ArrayList<Make> makes = fetchNetworkData();

            if(makes != null) {
                /*
                    Compare with existing data so we don't do unnecessary database operations.
                 */
                ArrayList<Model> existingModels = ProviderUtils.getModels(this);
                ArrayList<Model> downloadedModels = new ArrayList<>();

                List<ContentValues> contentValues = new ArrayList<>();

                for(Make make: makes) {
                    for(Model model: make.models) {
                        downloadedModels.add(model);
                        // If we don't already have this model in the database add it
                        if(!existingModels.contains(model)) {
                            ContentValues modelCv = new ContentValues();
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MAKE_NAME, make.name);
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MAKE_NICE_NAME, make.niceName);

                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MODEL_ID, model.id);
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MODEL_NAME, model.name);
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MODEL_NICE_NAME, model.niceName);
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_CURRENT_YEAR, model.currentYear);
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_BASE_PRICE, model.basePrice);
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_PHOTO_PATH, model.photoPath);

                            contentValues.add(modelCv);
                        }
                    }
                }

                /*
                    Delete any data that has been removed from the source.
                 */
                Collection<Model> removeVehicles = CollectionUtils.subtract(existingModels, downloadedModels);

                int deleteCount = 0;
                for(Model model: removeVehicles) {
                    deleteCount += getContentResolver()
                            .delete(CarculatorContract.Vehicle.buildModelUri(model.id), null, null);
                }

                /*
                    Add only new data to the database.
                 */
                int insertCount = getContentResolver().bulkInsert(CarculatorContract.Vehicle.CONTENT_URI,
                        contentValues.toArray(new ContentValues[contentValues.size()]));

                Timber.d("Inserted " + insertCount + " new model(s)");
                Timber.d("Deleted " + deleteCount + " model(s)");

                PreferenceUtils.updateLastUpdate(this, getString(R.string.pref_vehicle_last_update));
                sendStatusBroadcast(STATUS_SUCCESS, "Successfully completed");
            }
        } else {
            sendStatusBroadcast(STATUS_ERROR_NO_NETWORK, "No network connection");
        }
    }

    @Nullable
    private ArrayList<Make> fetchNetworkData() {
        // Simulate long latency for network request if in debug mode (10 seconds)
        if(BuildConfig.DEBUG) {
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        URL url;
        try {
            url = new URL(CONTENT_URL);
        } catch (MalformedURLException e) {
            Timber.e(e, e.getMessage());
            sendStatusBroadcast(STATUS_ERROR_NETWORK_ISSUE, e.getMessage());
            return null;
        }

        OkHttpClient client = NetworkManager.getClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                Gson gson = new Gson();
                try {
                    return gson.fromJson(response.body().charStream(), new TypeToken<ArrayList<Make>>(){}.getType());
                } catch (Exception e) {
                    Timber.e(e, "Problem processing vehicle JSON data");
                    sendStatusBroadcast(STATUS_ERROR_DATA_PROCESSING, e.toString());
                }
            } else {
                throw new IOException("Network error: " + response.code() + " -- " + response.body().string());
            }
        } catch(IOException e) {
            sendStatusBroadcast(STATUS_ERROR_NETWORK_ISSUE, e.toString());
        }
        return null;
    }

    private void sendStatusBroadcast(int code, @Nullable String message) {
        super.sendStatusBroadcast(code, message, getString(R.string.broadcast_sync_vehicle));
    }
}
