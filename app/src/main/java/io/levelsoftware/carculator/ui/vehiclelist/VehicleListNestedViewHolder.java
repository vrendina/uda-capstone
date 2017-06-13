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
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;

class VehicleListNestedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.linear_layout) LinearLayout linearLayout;
    @BindView(R.id.text_view_model_name) TextView nameTextView;
    @BindView(R.id.text_view_base_price) TextView basePriceTextView;
    @BindView(R.id.image_view_model_picture) ImageView modelImageView;
    @BindView(R.id.image_view_divider) ImageView dividerImagerView;

    private Make make;
    private Model model;

    public VehicleListNestedViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(this);
    }

    public void setVehicle(Model model, Make make) {
        this.model = model;
        this.make = make;

        nameTextView.setText(model.name);

        if(model.basePrice != null) {
            basePriceTextView.setText(model.basePrice);
            basePriceTextView.setVisibility(View.VISIBLE);
        }

        linearLayout.setContentDescription(make.name + " " + model.name);

        if(model.photoPath != null) {
            String imageUrl = "https://media.ed.edmunds-media.com" + model.photoPath + "150.jpg";

            Glide.with(modelImageView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .crossFade()
                    .into(modelImageView);
        }
    }
    
    public void showDivider(boolean visible) {
        dividerImagerView.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }
    

    @Override
    public void onClick(View view) {
        Context context = view.getContext();
        // Send click broadcast up to activity
        Intent intent = new Intent();
        intent.setAction(context.getString(R.string.broadcast_click_vehicle));

        intent.putExtra(context.getString(R.string.intent_key_quote_make), make);
        intent.putExtra(context.getString(R.string.intent_key_quote_model), model);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
