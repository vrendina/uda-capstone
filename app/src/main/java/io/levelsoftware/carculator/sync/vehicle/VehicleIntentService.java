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
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import io.levelsoftware.carculator.BuildConfig;
import io.levelsoftware.carculator.NetworkManager;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;
import io.levelsoftware.carculator.sync.BaseIntentService;
import io.levelsoftware.carculator.util.PreferenceUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


public class VehicleIntentService extends BaseIntentService {

    private static final String CONTENT_URL = "https://uda-capstone.firebaseapp.com/vehicles.json";
    private static final int MINIMUM_UPDATE_INTERVAL = 1000 * 60 * 1 ; // 1 minute

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
        if(networkIsAvailable()) {
            Make[] makes = fetchNetworkData();

            if(makes != null) {
                /*
                    Compare with existing data so we don't do unnecessary database operations.
                 */
                Collection<Integer> existingVehicles = new HashSet<>();

                Cursor cursor = getContentResolver().query(CarculatorContract.Vehicle.CONTENT_URI,
                        new String[]{CarculatorContract.Vehicle.COLUMN_EID}, null, null, null);

                if(cursor != null) {
                    for(int i = 0; i < cursor.getCount(); i++) {
                        if(cursor.moveToPosition(i)) {
                            Integer eid = cursor.getInt(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_EID));
                            existingVehicles.add(eid);
                        }
                    }
                    cursor.close();
                }

                List<ContentValues> contentValues = new ArrayList<>();
                Collection<Integer> downloadedVehicles = new HashSet<>();

                for(Make make: makes) {
                    for(Model model: make.getModels()) {
                        downloadedVehicles.add(model.getEid());
                        // If we don't already have this model in the database add it
                        if(!existingVehicles.contains(model.getEid())) {
                            ContentValues modelCv = new ContentValues();
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_EID, model.getEid());
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MAKE_EID, make.getEid());
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MAKE_NAME, make.getName());
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_MAKE_NICE_NAME, make.getNiceName());
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_NAME, model.getName());
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_NICE_NAME, model.getNiceName());
                            modelCv.put(CarculatorContract.Vehicle.COLUMN_YEAR, model.getYear());
                            contentValues.add(modelCv);
                        }
                    }
                }

                /*
                    Delete any data that has been removed from the source.
                 */
                Collection<Integer> removeVehicles = CollectionUtils.subtract(existingVehicles, downloadedVehicles);

                int deleteCount = 0;
                for(Integer key: removeVehicles) {
                    deleteCount += getContentResolver().delete(CarculatorContract.Vehicle.buildModelUri(key), null, null);
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
    private Make[] fetchNetworkData() {
        // Simulate long latency for network request (10 seconds)
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
                    return gson.fromJson(response.body().charStream(), Make[].class);
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
