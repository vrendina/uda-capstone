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

package io.levelsoftware.carculator.ui.quote;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.levelsoftware.carculator.R;

public class QuoteFormContainerAdapter extends RecyclerView.Adapter<QuoteFormContainerViewHolder> {

    @Override
    public QuoteFormContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.list_item_quote_form_container, parent, false);
        return new QuoteFormContainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuoteFormContainerViewHolder holder, int position) {
        holder.sectionTitleTextView.setText("Section " + position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
