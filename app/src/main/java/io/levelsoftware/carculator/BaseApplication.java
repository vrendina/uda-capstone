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

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import io.levelsoftware.carculator.util.NetworkManager;

public abstract class BaseApplication extends Application {
    abstract void configureLogging();

    @Override
    public void onCreate() {
        super.onCreate();

        configureLogging();
        NetworkManager.initializeNetworkManager();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
