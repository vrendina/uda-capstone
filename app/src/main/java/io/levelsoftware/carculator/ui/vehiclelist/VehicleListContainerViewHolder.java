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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.BuildConfig;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.Make;


public class VehicleListContainerViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_view_model_name)
    TextView makeTextView;
    @BindView(R.id.image_view_make_logo)
    ImageView makeLogoImageView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public Make make;
    public VehicleListNestedAdapter adapter;

    public VehicleListContainerViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void setMake(Make make) {
        this.make = make;

        Context context = this.itemView.getContext();

        adapter = new VehicleListNestedAdapter(make);
        recyclerView.setAdapter(adapter);

        int logoResourceId = context.getResources().getIdentifier("make_" + make.getNiceName(),
                "drawable", BuildConfig.APPLICATION_ID);

        makeLogoImageView.setImageResource(logoResourceId);
        makeTextView.setText(make.getName());
    }
}
