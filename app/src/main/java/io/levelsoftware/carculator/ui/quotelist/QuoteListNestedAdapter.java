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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;

public class QuoteListNestedAdapter extends RecyclerView.Adapter<QuoteListNestedViewHolder> {

    private ArrayList<Quote> quotes;

    public QuoteListNestedAdapter(@NonNull ArrayList<Quote> quotes) {
        this.quotes = quotes;
    }

    @Override
    public QuoteListNestedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_quote_element, parent, false);

        return new QuoteListNestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuoteListNestedViewHolder holder, int position) {
        holder.setQuote(quotes.get(position));

        // Show the divider unless we are at the last position
        holder.showDivider(position != getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        if(quotes != null) {
            return quotes.size();
        }
        return 0;
    }
}
