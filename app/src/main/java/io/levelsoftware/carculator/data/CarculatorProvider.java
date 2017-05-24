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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class CarculatorProvider extends ContentProvider {

    private CarculatorDatabaseHelper dbHelper;

    public static final int CODE_MODEL = 100;
    public static final int CODE_MODEL_WITH_ID = 101;
    public static final int CODE_MAKE = 102;
    public static final int CODE_MAKE_WITH_ID = 103;

    private static UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // content://io.levelsoftware.carculator/model
        matcher.addURI(CarculatorContract.CONTENT_AUTHORITY, CarculatorContract.Model.PATH,
                CODE_MODEL);

        // content://io.levelsoftware.carculator/model/#
        matcher.addURI(CarculatorContract.CONTENT_AUTHORITY, CarculatorContract.Model.PATH + "/#",
                CODE_MODEL_WITH_ID);

        // content://io.levelsoftware.carculator/make
        matcher.addURI(CarculatorContract.CONTENT_AUTHORITY, CarculatorContract.Make.PATH,
                CODE_MAKE);

        // content://io.levelsoftware.carculator/make/#
        matcher.addURI(CarculatorContract.CONTENT_AUTHORITY, CarculatorContract.Make.PATH + "/#",
                CODE_MAKE_WITH_ID);
    }
    
    @Override
    public boolean onCreate() {
        dbHelper = new CarculatorDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch(matcher.match(uri)) {
            case CODE_MAKE:
                cursor = db.query(CarculatorContract.Make.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MAKE_WITH_ID:
                selectionArgs = new String[]{uri.getLastPathSegment()};

                cursor = db.query(CarculatorContract.Make.TABLE_NAME,
                        projection,
                        CarculatorContract.Make.COLUMN_EID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MODEL:
                cursor = db.query(CarculatorContract.Model.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MODEL_WITH_ID:
                selectionArgs = new String[]{uri.getLastPathSegment()};

                cursor = db.query(CarculatorContract.Model.TABLE_NAME,
                        projection,
                        CarculatorContract.Model.COLUMN_EID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Invalid uri for query: " + uri);
        }

        Context context = getContext();
        if(context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String table;
        switch (matcher.match(uri)) {
            case CODE_MODEL:
                table = CarculatorContract.Model.TABLE_NAME;
                break;
            case CODE_MAKE:
                table = CarculatorContract.Make.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri for bulkInsert: " + uri);
        }

        db.beginTransaction();

        int insertCount = 0;
        try {
            for (ContentValues value : values) {
                db.insert(table, null, value);
                insertCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return insertCount;
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new RuntimeException("ContentProvider 'insert' will not be implemented, use bulkInsert");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(matcher.match(uri)) {
            case CODE_MAKE_WITH_ID:
                selectionArgs = new String[]{uri.getLastPathSegment()};

                numRowsDeleted = db.delete(
                        CarculatorContract.Make.TABLE_NAME,
                        CarculatorContract.Make.COLUMN_EID + " = ? ",
                        selectionArgs);
                break;

            case CODE_MODEL_WITH_ID:
                selectionArgs = new String[]{uri.getLastPathSegment()};

                numRowsDeleted = db.delete(
                        CarculatorContract.Model.TABLE_NAME,
                        CarculatorContract.Model.COLUMN_EID + " = ? ",
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Invalid uri for delete operation: " + uri);
        }

        if (numRowsDeleted != 0) {
            Context context = getContext();
            if(context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("ContentProvider 'update' will not be implemented, use delete then bulkInsert");
    }
}
