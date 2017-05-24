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


public class CarculatorDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "carculator";
    private static final int DATABASE_VERSION = 1;

    public CarculatorDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CarculatorContract.Make.TABLE_NAME + " ("
            + CarculatorContract.Make.COLUMN_EID       + " INTEGER NOT NULL,"
            + CarculatorContract.Make.COLUMN_NAME      + " TEXT NOT NULL,"
            + CarculatorContract.Make.COLUMN_NICE_NAME + " TEXT NOT NULL," +
        " PRIMARY KEY(" + CarculatorContract.Make.COLUMN_EID + "));");

        db.execSQL("CREATE TABLE " + CarculatorContract.Model.TABLE_NAME + " ("
                + CarculatorContract.Model.COLUMN_EID       + " INTEGER NOT NULL,"
                + CarculatorContract.Model.COLUMN_MAKE_ID   + " INTEGER NOT NULL,"
                + CarculatorContract.Model.COLUMN_NAME      + " TEXT NOT NULL,"
                + CarculatorContract.Model.COLUMN_NICE_NAME + " TEXT NOT NULL,"
                + CarculatorContract.Model.COLUMN_YEAR      + " INTEGER NOT NULL,"
                + " PRIMARY KEY(" + CarculatorContract.Model.COLUMN_EID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CarculatorContract.Make.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CarculatorContract.Model.TABLE_NAME);
        onCreate(db);
    }
}
