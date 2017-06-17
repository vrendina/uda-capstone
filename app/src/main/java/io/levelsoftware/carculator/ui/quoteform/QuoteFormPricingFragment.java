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

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import butterknife.BindView;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.ui.FormField;
import io.levelsoftware.keyculator.Keyculator;
import io.levelsoftware.keyculator.StringNumber;
import timber.log.Timber;


public abstract class QuoteFormPricingFragment extends QuoteFormFragment
        implements FormField.OnFormFieldEventListener,
        View.OnFocusChangeListener, Keyculator.OnEventListener {

    @BindView(R.id.form_container) LinearLayout container;
    @BindView(R.id.keyculator) Keyculator keyculator;
    @BindView(R.id.scroll_view) ScrollView scrollView;

    protected SparseArray<FormField> fields = new SparseArray<>();
    private int focused;

    public QuoteFormPricingFragment() {}

    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState);

    protected void collectFields(ViewGroup parent) {
        for(int i = 0; i<parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child instanceof FormField) {
                Timber.v("Found form field " + child.toString());
                fields.put(child.getId(), (FormField) child);
            } else if(child instanceof ViewGroup) {
                collectFields((ViewGroup) child);
            }
        }
    }

    protected void attachListeners() {
        for(int i = 0; i<fields.size(); i++) {
            FormField field = fields.get(fields.keyAt(i));
            field.setOnFormFieldEventListener(this);
        }

        container.setOnFocusChangeListener(this);
    }

    protected void setupFields() {
        collectFields(container);
        attachListeners();
    }

    protected void setupKeyboard() {
        keyculator.setScrollView(scrollView);
        keyculator.setEventListener(this);
    }

    @Override
    public void fieldFocusChanged(@IdRes int id, boolean hasFocus) {
        Timber.v("Focus changed for " + id + " has focus: " + hasFocus);
        if(hasFocus) {
            focused = id;

            FormField field = fields.get(id);
            Rect rect = new Rect(0, 0, field.getWidth(), field.getHeight());
            field.requestRectangleOnScreen(rect, false);

            showKeyboard((field.getValue() == null) ? null : field.getValue().getStringValue());
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        // If the user taps on the form container, hide the keyboard
        if(hasFocus && view == container) {
            if(keyculator.keyboardIsActive() || keyculator.keyboardIsEntering()) {
                keyculator.hideKeyboard();
            }
        }
    }

    protected boolean keyboardVisible() {
        return keyculator.keyboardIsActive() || keyculator.keyboardIsEntering();
    }

    protected void showKeyboard(@Nullable String value) {
        keyculator.showKeyboard(value);
    }
    protected void hideKeyboard() {
        container.requestFocus();
    }

    @Override public void keyboardOpened() {}
    @Override public void keyboardClosed() {}

    @Override
    public void keyboardResult(@NonNull StringNumber result) {
        FormField field = fields.get(focused);
        if(field != null) {
            field.setRawValue(result);
        }
    }

    @Override
    public void fieldValueChanged(@IdRes int id, StringNumber value) {

        Quote quote = quoteManager.getQuote();
        String string = value.getStringValue();

        switch (id) {
            case R.id.form_field_negotiated_price:
                if(!same(quote.price, string)) {
                    quote.price = string;
                    quote.edited = true;
                }
                break;

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

            case R.id.form_field_interest_rate:
                if(!same(quote.interestRate, string)) {
                    quote.interestRate = string;
                    quote.edited = true;
                }
                break;

            case R.id.form_field_term:
                if(!same(quote.term, string)) {
                    quote.term = string;
                    quote.edited = true;
                }
                break;

            case R.id.form_field_down_payment:
                if(!same(quote.downPayment, string)) {
                    quote.downPayment = string;
                    quote.edited = true;
                }
                break;

            case R.id.form_field_trade_in_value:
                if(!same(quote.tradeValue, string)) {
                    quote.tradeValue = string;
                    quote.edited = true;
                }
                break;

            case R.id.form_field_trade_in_owed:
                if(!same(quote.tradeOwed, string)) {
                    quote.tradeOwed = string;
                    quote.edited = true;
                }
                break;
        }
    }

    protected void setupQuote() {
        Quote quote = quoteManager.getQuote();

        if(quote != null) {
            fields.get(R.id.form_field_negotiated_price).setInitialValue(new StringNumber(quote.price));
            fields.get(R.id.form_field_down_payment).setInitialValue(new StringNumber(quote.downPayment));
            fields.get(R.id.form_field_term).setInitialValue(new StringNumber(quote.term));
            fields.get(R.id.form_field_trade_in_value).setInitialValue(new StringNumber(quote.tradeValue));
            fields.get(R.id.form_field_trade_in_owed).setInitialValue(new StringNumber(quote.tradeOwed));
        }
    }

}
