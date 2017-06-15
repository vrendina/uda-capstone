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
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;

public class QuoteListContainerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.container) LinearLayout container;
    @BindView(R.id.recycler_view) RecyclerView nestedRecyclerView;

    public ArrayList<Quote> quotes;
    public QuoteListNestedAdapter adapter;

    public QuoteListContainerViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void setQuotes(ArrayList<Quote> quotes) {
        this.quotes = quotes;

        adapter = new QuoteListNestedAdapter(quotes);
        nestedRecyclerView.setAdapter(adapter);

        container.setContentDescription(quotes.get(0).vehicle.make.name + " " +
                quotes.get(0).vehicle.model.name);
    }
}
