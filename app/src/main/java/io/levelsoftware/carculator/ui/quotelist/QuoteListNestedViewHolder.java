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
import io.levelsoftware.carculator.model.quote.Quote;

public class QuoteListNestedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.container) LinearLayout container;
    @BindView(R.id.text_view_total_price) TextView totalTextView;
    @BindView(R.id.text_view_dealer) TextView dealerTextView;
    @BindView(R.id.text_view_payment) TextView paymentTextView;
    @BindView(R.id.image_view_divider) ImageView dividerImagerView;

    private Quote quote;
    private Context context;

    public QuoteListNestedViewHolder(View view) {
        super(view);

        ButterKnife.bind(this, view);
        view.setOnClickListener(this);

        context = view.getContext();
    }

    public void setQuote(Quote quote) {
        this.quote = quote;

        dealerTextView.setText(context.getString(R.string.quote_no_dealer,
                quote.vehicle.make.name, quote.vehicle.model.name));


        paymentTextView.setText("Not enough information to display pricing");

        totalTextView.setVisibility(View.GONE);

        container.setContentDescription(quote.vehicle.make.name + " " + quote.vehicle.model.name);
    }

    public void showDivider(boolean visible) {
        dividerImagerView.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        // Send click broadcast up to activity

        Intent intent = new Intent();
        intent.setAction(context.getString(R.string.broadcast_click_quote));

        intent.putExtra(context.getString(R.string.intent_key_quote), quote);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}
