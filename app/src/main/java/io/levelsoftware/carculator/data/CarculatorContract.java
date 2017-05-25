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

package io.levelsoftware.carculator.data;

import android.net.Uri;

import io.levelsoftware.carculator.BuildConfig;

public class CarculatorContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Vehicle {
        public static final String PATH = "vehicle";
        public static final String TABLE_NAME = PATH;

        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String COLUMN_EID = "eid";
        public static final String COLUMN_MAKE_EID = "makeEid";
        public static final String COLUMN_MAKE_NAME = "makeName";
        public static final String COLUMN_MAKE_NICE_NAME ="makeNiceName";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NICE_NAME = "niceName";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_BASE_PRICE = "basePrice";

        /**
         * Fetch Uri for a vehicle using the Edmunds model id
         *
         * @param eid Edmunds id for the requested model
         * @return Uri
         */
        public static Uri buildModelUri(long eid) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(eid)).build();
        }
    }
}
