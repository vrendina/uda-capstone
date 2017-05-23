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

import io.levelsoftware.carculator.R;
import timber.log.Timber;


public class VehicleIntentService extends BaseIntentService {

    private static final String SERVICE_NAME = "VehicleService";
    private static boolean isRunning;

    public VehicleIntentService() {
        super(SERVICE_NAME);
    }

    public static synchronized boolean isRunning() {
        return isRunning;
    }

    protected static synchronized void setIsRunning(boolean isRunning) {
        VehicleIntentService.isRunning = isRunning;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("Running vehicle synchronization...");
        setIsRunning(true);

        // Do work

        sendStatusBroadcast(STATUS_COMPLETE, null);
        Timber.d("Vehicle synchronization complete.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setIsRunning(false);
    }

    public static void start(Context context) {
        context.startService(new Intent(context, VehicleIntentService.class));
    }

    private void sendStatusBroadcast(int code, @Nullable String message) {
        super.sendStatusBroadcast(code, message, getString(R.string.broadcast_sync_vehicle));
    }
}
