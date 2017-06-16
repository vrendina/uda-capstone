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

package io.levelsoftware.carculator.ui.quoteform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;


public class QuoteFormLeasePricingFragment extends QuoteFormPricingFragment {

    public static QuoteFormLeasePricingFragment newInstance(Bundle arguments) {
        QuoteFormLeasePricingFragment fragment = new QuoteFormLeasePricingFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quote_lease_form, container, false);
        ButterKnife.bind(this, view);

        setupFields();
        setupKeyboard();

        return view;
    }

    @Override
    protected void setQuote(Quote quote) {
        super.setQuote(quote);

        // If the quote doesn't have a created date saved, setup some defaults
        if(TextUtils.isEmpty(quote.created) && !quote.edited) {

        }
    }


}
