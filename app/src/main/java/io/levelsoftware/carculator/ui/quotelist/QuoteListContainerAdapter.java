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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;

public class QuoteListContainerAdapter extends RecyclerView.Adapter<QuoteListContainerViewHolder> {

    private HashMap<String, ArrayList<Quote>> data = new LinkedHashMap<>();
    private String[] keys;

    @Override
    public QuoteListContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_quote_container, parent, false);

        return new QuoteListContainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuoteListContainerViewHolder holder, int position) {
        holder.setQuotes(data.get(keys[position]));
    }

    @Override
    public int getItemCount() {
        return (keys == null) ? 0 : keys.length;
    }

    public void setData(HashMap<String, ArrayList<Quote>> data) {
        this.data = data;
        keys = data.keySet().toArray(new String[data.keySet().size()]);

        notifyDataSetChanged();
    }
}
