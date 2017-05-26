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

package io.levelsoftware.carculator.ui.vehiclelist;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;
import timber.log.Timber;


public class VehicleListContainerAdapter extends
        RecyclerView.Adapter<VehicleListContainerViewHolder> {

    private Make[] data;
    private Make[] filteredData;

    private OnClickListener listener;

    public VehicleListContainerAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public VehicleListContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_vehicle_container, parent, false);

        return new VehicleListContainerViewHolder(view);
    }

    public void setCursor(Cursor cursor) {
        if(cursor != null && cursor.getCount() > 0) {

            LinkedHashMap<Make, ArrayList<Model>> dataBuilder = new LinkedHashMap<>();

            for(int i = 0; i<cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                Make make = new Make();
                make.setEid(cursor.getInt(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_EID)));

                if (!dataBuilder.containsKey(make)) {
                    make.setName(cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_NAME)));
                    make.setNiceName(cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_NICE_NAME)));
                    dataBuilder.put(make, new ArrayList<Model>());
                }

                Model model = new Model();
                model.setEid(cursor.getInt(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_EID)));
                model.setName(cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_NAME)));
                model.setNiceName(cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_NICE_NAME)));
                model.setYear(cursor.getInt(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_YEAR)));
                model.setBasePrice(cursor.getInt(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_BASE_PRICE)));

                dataBuilder.get(make).add(model);
            }

            for(Make make: dataBuilder.keySet()) {
                make.setModels(dataBuilder.get(make));
            }
            data = dataBuilder.keySet().toArray(new Make[dataBuilder.keySet().size()]);
            filteredData = data.clone();

            notifyDataSetChanged();
        }
    }

    // TODO: Performance optimization
    public void filter(@NonNull String query) {
        Timber.d("Query string: '" + query + "'");
        if(TextUtils.isEmpty(query.replaceAll("[^A-Za-z0-9]", ""))) {
            filteredData = data.clone();
        } else {
            // Split the query string based on whitespace and use an AND operator on each element
            String[] queries = query.split("\\s");

//            ArrayList<String> suggestions = new ArrayList<>();
            LinkedHashMap<Make, ArrayList<Model>> dataBuilder = new LinkedHashMap<>();

            for (Make make : data) {
                for (Model model : make.getModels()) {
                    String readableVehicleString = model.getYear() + " " + make.getName() + " " + model.getName();
                    String vehicleString = readableVehicleString.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ENGLISH);

                    // Match all parts of the query string
                    for(int i = 0; i < queries.length; i++) {
                        String q = queries[i].replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ENGLISH);

                        if(vehicleString.contains(q)) {
                            // If we have matched all of the queries then add the make to the list
                            if(i == queries.length - 1) {
//                                suggestions.add(readableVehicleString);

                                Make filteredMake = new Make();
                                filteredMake.setEid(make.getEid());

                                if (!dataBuilder.containsKey(filteredMake)) {
                                    filteredMake.setName(make.getName());
                                    filteredMake.setNiceName(make.getNiceName());
                                    dataBuilder.put(filteredMake, new ArrayList<Model>());
                                }
                                dataBuilder.get(filteredMake).add(model);

                                Timber.d(readableVehicleString);
                            }
                            continue;
                        }
                        // If we don't match part of the query break out of the loop
                        break;
                    }
                }
            }

            for(Make make: dataBuilder.keySet()) {
                make.setModels(dataBuilder.get(make));
            }

            filteredData = dataBuilder.keySet().toArray(new Make[dataBuilder.keySet().size()]);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(VehicleListContainerViewHolder holder, int position) {
        holder.setMake(filteredData[position]);
    }

    @Override
    public int getItemCount() {
        if(filteredData != null) {
            return filteredData.length;
        }
        return 0;
    }


    public interface OnClickListener {
        void clickVehicle();
    }
}
