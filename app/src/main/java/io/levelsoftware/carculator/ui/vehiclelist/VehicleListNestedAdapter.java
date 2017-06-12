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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.Make;

public class VehicleListNestedAdapter extends
        RecyclerView.Adapter<VehicleListNestedViewHolder> {

    private Make data;

    public VehicleListNestedAdapter(@NonNull Make data) {
        this.data = data;
    }

    @Override
    public VehicleListNestedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_vehicle_element, parent, false);

        return new VehicleListNestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VehicleListNestedViewHolder holder, int position) {
        holder.setVehicle(data.models.get(position), data);
        // Show the divider unless we are at the last position
        holder.showDivider(position != getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        if(data != null && data.models != null) {
            return data.models.size();
        }
        return 0;
    }

}