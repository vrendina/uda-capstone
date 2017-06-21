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

package io.levelsoftware.carculator.widget;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.util.FormatUtils;
import io.levelsoftware.carculator.util.PreferenceUtils;
import timber.log.Timber;

public class QuoteListRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private ArrayList<Quote> data = new ArrayList<>();

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                String recentQuotes = PreferenceUtils.getRecentQuotes(getApplicationContext(),
                        getString(R.string.widget_pref_recent_quotes));

                if(!TextUtils.isEmpty(recentQuotes)) {
                    Gson gson = new Gson();
                    data = gson.fromJson(recentQuotes, new TypeToken<ArrayList<Quote>>() {
                    }.getType());
                }

                Timber.d("Got recent quotes: " + recentQuotes);

            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                Quote quote = data.get(position);

                if (position == AdapterView.INVALID_POSITION || quote == null) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_quote_list_item);

                String dealer = getString(R.string.quote_no_dealer,
                        quote.vehicle.make.name, quote.vehicle.model.name);

                String payment = getString(R.string.quote_list_pricing,
                        FormatUtils.formatCurrency(new BigDecimal(quote.monthlyPayment)),
                        FormatUtils.formatCurrency(new BigDecimal(quote.dueAtSigning)));

                views.setTextViewText(R.id.text_view_dealer, dealer);
                views.setTextViewText(R.id.text_view_pricing, payment);

                views.setContentDescription(R.id.linear_layout, dealer);

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(getString(R.string.intent_key_quote), quote);
                fillInIntent.putExtra(getString(R.string.intent_key_widget_launch), true);

                views.setOnClickFillInIntent(R.id.linear_layout, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
