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

package io.levelsoftware.carculator.ui.quotelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.model.quote.Vehicle;

public class QuoteListContainerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.container) LinearLayout container;
    @BindView(R.id.recycler_view) RecyclerView nestedRecyclerView;
    @BindView(R.id.image_view_model_picture) ImageView modelImageView;

    private Context context;

    public QuoteListContainerViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

        context = view.getContext();
    }

    public void setQuotes(ArrayList<Quote> quotes) {
        Vehicle vehicle = quotes.get(0).vehicle;

        QuoteListNestedAdapter adapter = new QuoteListNestedAdapter(quotes);
        nestedRecyclerView.setAdapter(adapter);

        if(vehicle.model.photoPath != null) {
            String imageUrl = context.getString(R.string.edmunds_base_media_url)
                    + vehicle.model.photoPath
                    + context.getString(R.string.edmunds_media_full_suffix);

            Glide.with(modelImageView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .crossFade()
                    .into(modelImageView);
        }

        container.setContentDescription(vehicle.make.name + " " +
                vehicle.model.name);
    }
}
