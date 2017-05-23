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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import io.levelsoftware.carculator.R;


public abstract class BaseIntentService extends IntentService {

    public static final int STATUS_COMPLETE = 100;

    public static final int STATUS_ERROR_NO_NETWORK = 900;
    public static final int STATUS_ERROR_NETWORK = 901;

    public BaseIntentService(String name) {
        super(name);
    }

    protected boolean networkIsAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    protected void sendStatusBroadcast(int code, @Nullable String message, @NonNull String action) {
        Intent intent = new Intent();
        intent.setAction(action);

        intent.putExtra(getString(R.string.key_status_code), code);
        intent.putExtra(getString(R.string.key_status_message), message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
