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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.sync.vehicle.VehicleIntentService;


public class SyncBroadcastReceiver extends BroadcastReceiver {

    OnStatusUpdateListener listener;

    public SyncBroadcastReceiver(OnStatusUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int code = intent.getIntExtra(context.getString(R.string.key_status_code), -1);
        String message = intent.getStringExtra(context.getString(R.string.key_status_message));

        if(code == VehicleIntentService.STATUS_SUCCESS) {
            listener.statusSuccess();
        } else {
            listener.statusError(code, message);
        }
    }

    public interface OnStatusUpdateListener {
        void statusSuccess();
        void statusError(int code, @Nullable String message);
    }
}
