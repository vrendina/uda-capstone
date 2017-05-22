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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class VehicleDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "carculator.db";
    private static final int DATABASE_VERSION = 1;

    public VehicleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + VehicleContract.Make.TABLE_NAME + " ("
            + VehicleContract.Make.COLUMN_EID       + " INTEGER NOT NULL,"
            + VehicleContract.Make.COLUMN_NAME      + " TEXT NOT NULL,"
            + VehicleContract.Make.COLUMN_NICE_NAME + " TEXT NOT NULL," +
        " PRIMARY KEY(" + VehicleContract.Make.COLUMN_EID + "));");

        db.execSQL("CREATE TABLE " + VehicleContract.Model.TABLE_NAME + " ("
                + VehicleContract.Model.COLUMN_EID       + " INTEGER NOT NULL,"
                + VehicleContract.Model.COLUMN_MAKE_ID   + " INTEGER NOT NULL,"
                + VehicleContract.Model.COLUMN_NAME      + " TEXT NOT NULL,"
                + VehicleContract.Model.COLUMN_NICE_NAME + " TEXT NOT NULL,"
                + VehicleContract.Model.COLUMN_YEAR      + " INTEGER NOT NULL,"
                + " PRIMARY KEY(" + VehicleContract.Model.COLUMN_EID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.Make.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VehicleContract.Model.TABLE_NAME);
        onCreate(db);
    }
}
