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

import android.support.v4.content.LocalBroadcastManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import timber.log.Timber;

public class VehicleJobService extends JobService
        implements VehicleBroadcastReceiver.OnStatusUpdateListener {

    VehicleBroadcastReceiver receiver;
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
        receiver = new VehicleBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                VehicleBroadcastReceiver.getFilter(this));
    }

    private void unBindReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void statusComplete() {
        unBindReceiver();
        jobFinished(params, false);
    }

    @Override
    public void statusErrorNetwork(String message) {
        Timber.d("Got status network error: " + message);
        unBindReceiver();
        jobFinished(params, true);
    }

    @Override
    public void statusErrorUnknown(int code, String message) {
        Timber.d("Got status unknown error (" + code + "): " + message);
        unBindReceiver();
        jobFinished(params, true);
    }
}
