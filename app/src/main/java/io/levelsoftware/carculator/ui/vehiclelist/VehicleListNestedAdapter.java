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
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;

public class VehicleListNestedAdapter extends
        RecyclerView.Adapter<VehicleListNestedAdapter.VehicleListNestedViewHolder> {

    private Make data;

    public VehicleListNestedAdapter(@NonNull Make data) {
        this.data = data;
    }

    @Override
    public VehicleListNestedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_vehicle, parent, false);

        return new VehicleListNestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VehicleListNestedViewHolder holder, int position) {
        Model model = data.getModels().get(position);
        holder.testTextView.setText(model.getYear() + " " + model.getName());
    }

    @Override
    public int getItemCount() {
        if(data != null && data.getModels() != null) {
            return data.getModels().size();
        }
        return 0;
    }

    class VehicleListNestedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.test_text) TextView testTextView;

        public VehicleListNestedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}