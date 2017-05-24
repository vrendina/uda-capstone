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

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.data.PreferenceUtils;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;
import io.levelsoftware.carculator.sync.BaseIntentService;
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
                Collection<Integer> existingMakes = new HashSet<>();
                Collection<Integer> existingModels = new HashSet<>();

                Cursor makeCursor = getContentResolver().query(CarculatorContract.Make.CONTENT_URI,
                        new String[]{CarculatorContract.Make.COLUMN_EID}, null, null, null);

                if(makeCursor != null) {
                    for(int i = 0; i < makeCursor.getCount(); i++) {
                        if(makeCursor.moveToPosition(i)) {
                            Integer eid = makeCursor.getInt(makeCursor.getColumnIndex(CarculatorContract.Make.COLUMN_EID));
                            existingMakes.add(eid);
                        }
                    }
                    makeCursor.close();
                }

                Cursor modelCursor = getContentResolver().query(CarculatorContract.Model.CONTENT_URI,
                        new String[]{CarculatorContract.Model.COLUMN_EID}, null, null, null);

                if(modelCursor != null) {
                    for(int i = 0; i < modelCursor.getCount(); i++) {
                        if(modelCursor.moveToPosition(i)) {
                            Integer eid = modelCursor.getInt(modelCursor.getColumnIndex(CarculatorContract.Model.COLUMN_EID));
                            existingModels.add(eid);
                        }
                    }
                    modelCursor.close();
                }

                List<ContentValues> makesCv = new ArrayList<>();
                List<ContentValues> modelsCv = new ArrayList<>();

                Collection<Integer> downloadedMakes = new HashSet<>();
                Collection<Integer> downloadedModels = new HashSet<>();
                for(Make make: makes) {
                    downloadedMakes.add(make.getEid());
                    // If we don't already have this make in the database add it
                    if(!existingMakes.contains(make.getEid())) {
                        ContentValues makeCv = new ContentValues();
                        makeCv.put(CarculatorContract.Make.COLUMN_EID, make.getEid());
                        makeCv.put(CarculatorContract.Make.COLUMN_NAME, make.getName());
                        makeCv.put(CarculatorContract.Make.COLUMN_NICE_NAME, make.getNiceName());
                        makesCv.add(makeCv);
                    }

                    for(Model model: make.getModels()) {
                        downloadedModels.add(model.getEid());
                        // If we don't already have this model in the database add it
                        if(!existingModels.contains(model.getEid())) {
                            ContentValues modelCv = new ContentValues();
                            modelCv.put(CarculatorContract.Model.COLUMN_EID, model.getEid());
                            modelCv.put(CarculatorContract.Model.COLUMN_MAKE_ID, make.getEid());
                            modelCv.put(CarculatorContract.Model.COLUMN_NAME, model.getName());
                            modelCv.put(CarculatorContract.Model.COLUMN_NICE_NAME, model.getNiceName());
                            modelCv.put(CarculatorContract.Model.COLUMN_YEAR, model.getYear());
                            modelsCv.add(modelCv);
                        }
                    }
                }

                Collection<Integer> removeMakes = CollectionUtils.subtract(existingMakes, downloadedMakes);
                Collection<Integer> removeModels = CollectionUtils.subtract(existingModels, downloadedModels);

//                Timber.d("Existing makes -- " + existingMakes);
//                Timber.d("Downloaded makes --" + downloadedMakes);
//                Timber.d("Existing models --" + existingModels);
//                Timber.d("Downloaded models --" + downloadedModels);
//                Timber.d("Remove makes -- " + removeMakes.toString());
//                Timber.d("Remove models -- " + removeModels.toString());

                /*
                    Delete any data that has been removed from the source.
                 */
                int makeDeleteCount = 0;
                for(Integer key: removeMakes) {
                    makeDeleteCount += getContentResolver().delete(CarculatorContract.Make.buildMakeUri(key), null, null);
                }
                int modelDeleteCount = 0;
                for(Integer key: removeModels) {
                    modelDeleteCount += getContentResolver().delete(CarculatorContract.Model.buildModelUri(key), null, null);
                }

                /*
                    Add only new data to the database.
                 */
                int makeInsertCount = getContentResolver().bulkInsert(CarculatorContract.Make.CONTENT_URI,
                        makesCv.toArray(new ContentValues[makesCv.size()]));
                int modelInsertCount = getContentResolver().bulkInsert(CarculatorContract.Model.CONTENT_URI,
                        modelsCv.toArray(new ContentValues[modelsCv.size()]));

                Timber.d("Inserted " + makeInsertCount + " new make(s)");
                Timber.d("Inserted " + modelInsertCount + " new model(s)");
                Timber.d("Deleted " + makeDeleteCount + " make(s)");
                Timber.d("Deleted " + modelDeleteCount + " model(s)");

                PreferenceUtils.updateLastUpdate(this, getString(R.string.pref_vehicle_last_update));
                sendStatusBroadcast(STATUS_SUCCESS, null);
            }
        } else {
            sendStatusBroadcast(STATUS_ERROR_NO_NETWORK, "No network connection");
        }
    }

    @Nullable
    private Make[] fetchNetworkData() {
        URL url;
        try {
            url = new URL(CONTENT_URL);
        } catch (MalformedURLException e) {
            Timber.e(e, e.getMessage());
            sendStatusBroadcast(STATUS_ERROR_NETWORK_ISSUE, e.getMessage());
            return null;
        }

        OkHttpClient client = new OkHttpClient();
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
