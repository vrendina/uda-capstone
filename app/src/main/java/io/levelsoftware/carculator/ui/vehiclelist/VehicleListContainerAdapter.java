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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;


public class VehicleListContainerAdapter extends
        RecyclerView.Adapter<VehicleListContainerAdapter.VehicleListContainerViewHolder> {

    private Make[] data;

    @Override
    public VehicleListContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_vehicle_container, parent, false);

        return new VehicleListContainerViewHolder(view);
    }

    public void setCursor(Cursor cursor) {
        /*
            This processing will likely need to be put in a background task. This is what not
            being allowed to use an ORM looks like.
         */
        if(cursor != null && cursor.getCount() > 0) {

            LinkedHashMap<Make, ArrayList<Model>> dataBuilder = new LinkedHashMap<>();

            for(int i = 0; i<cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                Make make = new Make();
                make.setEid(cursor.getInt(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_EID)));

                if (!dataBuilder.containsKey(make)) {
                    make.setName(cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_NAME)));
                    make.setNiceName(cursor.getString(cursor.getColumnIndex(CarculatorContract.Vehicle.COLUMN_MAKE_NICE_NAME)));

                    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") ArrayList<Model> models = new ArrayList<>();
                    dataBuilder.put(make, models);
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

            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(VehicleListContainerViewHolder holder, int position) {
        Make make = data[position];

        holder.adapter = new VehicleListNestedAdapter(make);
        holder.recyclerView.setAdapter(holder.adapter);

        holder.testTextView.setText(make.getName());
    }

    @Override
    public int getItemCount() {
        if(data != null) {
            return data.length;
        }
        return 0;
    }

    class VehicleListContainerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.test_text) TextView testTextView;
        @BindView(R.id.recycler_view) RecyclerView recyclerView;

        VehicleListNestedAdapter adapter;

        public VehicleListContainerViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}
