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

package io.levelsoftware.carculator.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.Date;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.PreferenceUtils;
import timber.log.Timber;


public class VehicleIntentService extends BaseIntentService {

    public static final int MINIMUM_UPDATE_INTERVAL = 1000 * 60 * 2 ; // 2 minutes
    //public static final int MINIMUM_UPDATE_INTERVAL = 1000 * 60 * 60 * 24 * 7 ; // 7 days

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

        if(dataNeedsUpdate()) {
            updateData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("Vehicle intent service destroyed");
        setIsRunning(false);
    }

    private boolean dataNeedsUpdate() {
        long lastUpdate = PreferenceUtils.getLastUpdate(this, getString(R.string.pref_vehicle_last_update));
        long currentDate = (new Date()).getTime();

        long elapsed = currentDate - lastUpdate;

        if(elapsed < MINIMUM_UPDATE_INTERVAL) {
            sendStatusBroadcast(STATUS_ERROR_DATA_CURRENT,
                    "Data up to date. Minimum interval: "
                            + MINIMUM_UPDATE_INTERVAL + " Elapsed time: " + elapsed);
            return false;
        }
        return true;
    }

    private void updateData() {
        if(networkIsAvailable()) {


            PreferenceUtils.updateLastUpdate(this, getString(R.string.pref_vehicle_last_update));
            sendStatusBroadcast(STATUS_SUCCESS, null);
        } else {
            sendStatusBroadcast(STATUS_ERROR_NO_NETWORK, "No network connection");
        }
    }

    private void sendStatusBroadcast(int code, @Nullable String message) {
        super.sendStatusBroadcast(code, message, getString(R.string.broadcast_sync_vehicle));
    }
}
