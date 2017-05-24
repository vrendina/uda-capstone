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

import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.sync.SyncBroadcastReceiver;
import timber.log.Timber;

public class VehicleJobService extends JobService
        implements SyncBroadcastReceiver.OnStatusUpdateListener {

    SyncBroadcastReceiver receiver;
    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        Timber.v("Starting scheduled vehicle synchronization job...");
        this.params = params;
        bindReceiver();
        VehicleIntentService.start(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void bindReceiver() {
        receiver = new SyncBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(getString(R.string.broadcast_sync_vehicle)));
    }

    private void unBindReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void statusSuccess() {
        Timber.d("Scheduled vehicle update completed successfully.");
        unBindReceiver();
        jobFinished(params, false);
    }

    @Override
    public void statusError(int code, @Nullable String message) {
        Timber.d("Error during scheduled vehicle update. Code: " + code + " Message: " + message);
        unBindReceiver();
        jobFinished(params, true);
    }
}
