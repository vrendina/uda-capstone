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

package io.levelsoftware.carculator.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.ui.quotelist.QuoteListActivity;
import timber.log.Timber;


public class SplashActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

    public static final int REQUEST_PLAY_SERVICES_CODE = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    private void checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);

        if(code == ConnectionResult.SUCCESS) {
            onActivityResult(REQUEST_PLAY_SERVICES_CODE, Activity.RESULT_OK, null);
        } else {
            Timber.w("Google Play Services Error: " + api.getErrorString(code));
            if(api.isUserResolvableError(code)) {
                api.getErrorDialog(this, code, REQUEST_PLAY_SERVICES_CODE, this).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PLAY_SERVICES_CODE) {
            // If we are good on the play services, launch the first activity
            if(resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(this, QuoteListActivity.class);
                startActivity(intent);
            // If we don't have a proper play services setup, close
            } else {
                this.finish();
            }
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        onActivityResult(REQUEST_PLAY_SERVICES_CODE, Activity.RESULT_CANCELED, null);
    }
}
