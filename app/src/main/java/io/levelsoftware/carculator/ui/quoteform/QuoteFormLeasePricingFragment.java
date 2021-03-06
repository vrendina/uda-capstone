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
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.fincalc.LeaseCalculator;
import io.levelsoftware.keyculator.StringNumber;
import timber.log.Timber;


public class QuoteFormLeasePricingFragment extends QuoteFormPricingFragment {

    protected LeaseCalculator calculator;

    public QuoteFormLeasePricingFragment() {}

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
        setupQuote();

        return view;
    }


    @Override
    protected void setupQuote() {
        super.setupQuote();

        Quote quote = quoteManager.getQuote();

        if(quote != null) {
            fields.get(R.id.form_field_residual_value).setInitialValue(new StringNumber(quote.residual));
            fields.get(R.id.form_field_money_factor).setInitialValue(new StringNumber(quote.moneyFactor));

            calculator = new LeaseCalculator();
        }
    }

    @Override
    public void fieldValueChanged(@IdRes int id, StringNumber value) {
        Quote quote = quoteManager.getQuote();
        String string = value.getStringValue();

        Timber.d("Field with id '" + id + "' changed value to: " + string);

        switch (id) {
            case R.id.form_field_residual_value:
                if(!same(quote.residual, string)) {
                    quote.residual = string;
                    quote.edited = true;
                }
                break;

            case R.id.form_field_money_factor:
                if(!same(quote.moneyFactor, string)) {
                    quote.moneyFactor = string;
                    quote.edited = true;
                }
                break;
        }

        updateCalculator();
    }
}
