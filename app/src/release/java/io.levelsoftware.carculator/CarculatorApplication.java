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

package io.levelsoftware.carculator;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class CarculatorApplication extends BaseApplication {

    @Override
    void configureLogging() {
        Timber.plant(new FirebaseReportingTree());
    }

    private class FirebaseReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable throwable) {
            if(priority >= Log.WARN) {
                FirebaseCrash.log(message);
                if(throwable != null) {
                    FirebaseCrash.report(throwable);
                }
            }
        }
    }
}
