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

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class NetworkManager {

    private static NetworkManager manager;
    private static OkHttpClient client;

    private NetworkManager() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new StethoInterceptor())
                .build();
    }

    public static void initializeNetworkManager() {
        if(manager == null) {
            manager = new NetworkManager();
        }
    }

    public static OkHttpClient getClient() {
        if(client == null) {
            initializeNetworkManager();
        }
        return client;
    }
}
