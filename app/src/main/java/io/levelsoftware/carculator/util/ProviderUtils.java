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

package io.levelsoftware.carculator.util;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;

import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;

/**
 * This class is basically functioning as our ORM since we can't use a library like Realm
 * for storing data as part of this project.
 */
public class ProviderUtils {

    /**
     * Fetch all vehicle makes from the ContentProvider and return it as an ArrayList of Make
     * objects with their associated models.
     *
     * @param context Context for retrieving ContentResolver
     * @return ArrayList<Make>
     */
    public static ArrayList<Make> getMakes(@NonNull Context context) {
        Cursor cursor = context.getContentResolver()
                .query(CarculatorContract.Vehicle.CONTENT_URI, null, null, null, null);

        ArrayList<Make> makes = new ArrayList<>();

        if (cursor != null) {
            makes = ProviderUtils.getMakes(cursor);
            cursor.close();
        }

        return makes;
    }

    /**
     * {@link #getMakes(Context)}
     */
    public static ArrayList<Make> getMakes(Cursor cursor) {

        ArrayList<Make> makes = new ArrayList<>();
        SparseArray<Make> map = new SparseArray<>();

        if(cursor != null) {
            for(int i = 0; i < cursor.getCount(); i++) {
                if(cursor.moveToPosition(i)) {
                    String makeName = cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_NAME));
                    String makeNiceName = cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_NICE_NAME));
                    String modelId = cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MODEL_ID));
                    String modelName = cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MODEL_NAME));
                    String modelNiceName = cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MODEL_NICE_NAME));
                    Integer currentYear = cursor.getInt(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_CURRENT_YEAR));
                    String photoPath = cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_PHOTO_PATH));
                    String basePrice = cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_BASE_PRICE));

                    int key = makeNiceName.hashCode();
                    Make make = map.get(key);
                    if(make == null) {
                        make = new Make();
                        make.name = makeName;
                        make.niceName = makeNiceName;
                        make.models = new ArrayList<>();

                        map.put(key, make);
                        makes.add(make);
                    }

                    Model model = new Model();

                    model.id = modelId;
                    model.name = modelName;
                    model.niceName = modelNiceName;
                    model.currentYear = currentYear;
                    model.photoPath = photoPath;
                    model.basePrice = basePrice;

                    make.models.add(model);
                }
            }
        }

        return makes;
    }

    /**
     * Fetch all vehicle models from the ContentProvider and return as an ArrayList of Model
     * objects.
     *
     * @param context Context for retrieving ContentResolver
     * @return ArrayList<Model>
     */
    public static ArrayList<Model> getModels(@NonNull Context context) {
        ArrayList<Make> makes = ProviderUtils.getMakes(context);

        ArrayList<Model> models = new ArrayList<>();

        for(Make make: makes) {
            for(Model model: make.models) {
                models.add(model);
            }
        }

        return models;
    }

}
