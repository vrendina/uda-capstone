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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;
import timber.log.Timber;

class VehicleListNestedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.linear_layout) LinearLayout linearLayout;
    @BindView(R.id.text_view_model_name) TextView nameTextView;
    @BindView(R.id.text_view_base_price) TextView basePriceTextView;
    @BindView(R.id.image_view_model_picture) ImageView modelImageView;
    @BindView(R.id.image_view_divider) ImageView dividerImagerView;

    public VehicleListNestedViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(this);
    }

    public void setVehicle(Model model, Make make) {
        nameTextView.setText(model.getYear() + " " + model.getName());

        if(model.getBasePrice() != null && model.getBasePrice() != 0) {
            basePriceTextView.setText(model.getBasePrice().toString());
            basePriceTextView.setVisibility(View.VISIBLE);
        }

        linearLayout.setContentDescription(model.getYear() + " " + make.getName() + " " + model.getName());
    }
    
    public void showDivider(boolean visible) {
        dividerImagerView.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }
    

    @Override
    public void onClick(View view) {
        Timber.d("Clicked on view at position -- " + getAdapterPosition());

        Context context = view.getContext();
        // Send click broadcast up to activity
        Intent intent = new Intent();
        intent.setAction(context.getString(R.string.broadcast_click_vehicle));

//        intent.putExtra(getString(R.string.key_status_code), code);
//        intent.putExtra(getString(R.string.key_status_message), message);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
