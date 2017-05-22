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

public class VehicleContract {

    public static final String CONTENT_AUTHORITY = "io.levelsoftware.carculator";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Make {
        public static final String PATH = "make";
        public static final String TABLE_NAME = PATH;

        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String COLUMN_EID = "eid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NICE_NAME = "niceName";

        /**
         * Fetch Uri for a vehicle make using the Edmunds id
         *
         * @param eid Edmunds id for the requested make
         * @return Uri
         */
        public static Uri buildMakeUri(long eid) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(eid)).build();
        }
    }

    public static final class Model {
        public static final String PATH = "model";
        public static final String TABLE_NAME = PATH;

        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String COLUMN_EID = "eid";
        public static final String COLUMN_MAKE_ID = "makeId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NICE_NAME = "niceName";
        public static final String COLUMN_YEAR = "year";

        /**
         * Fetch Uri for a vehicle model using the Edmunds id
         *
         * @param eid Edmunds id for the requested model
         * @return Uri
         */
        public static Uri buildModelUri(long eid) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(eid)).build();
        }
    }
}
