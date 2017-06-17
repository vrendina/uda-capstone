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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.model.quote.Vehicle;
import io.levelsoftware.carculator.util.DateUtils;
import io.levelsoftware.carculator.util.UserUtils;
import timber.log.Timber;

public class QuoteFormActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, QuoteFormFragment.QuoteManager {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;

    @BindView(R.id.linear_layout_toolbar_price) LinearLayout toolbarPrice;
    @BindView(R.id.text_view_toolbar_price) TextView toolbarPriceTextView;
    @BindView(R.id.text_view_toolbar_price_label) TextView toolbarPriceLabelTextView;

    private QuoteFormPricingFragment pricingFragment;

    private Quote quote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_form);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState != null) {
            quote = savedInstanceState.getParcelable(getString(R.string.intent_key_quote));
        } else {
            quote = getIntent().getParcelableExtra(getString(R.string.intent_key_quote));
        }

        // If we don't have a quote, create a new object
        if(quote == null) {
            Vehicle vehicle = getIntent().getParcelableExtra(getString(R.string.intent_key_quote_vehicle));
            quote = new Quote(vehicle);
            quote.type = getIntent().getStringExtra(getString(R.string.intent_key_quote_type));
        }

        setupTabs();
        setupToolbar();
    }

    @Override
    public void onBackPressed() {
        // Close the keyboard on back button press if it is open
        if(pricingFragment.keyboardVisible()) {
            pricingFragment.hideKeyboard();
            return;
        }

        // Save the quote if it has changes, otherwise return to select a vehicle
        if(quote.edited) {
            if(saveQuote()) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.intent_key_quote), quote);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.removeOnPageChangeListener(this);
    }

    private boolean saveQuote() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String userId = UserUtils.getInstance().getUid();

        // If we don't have a quote id, the quote has not been saved previously
        if(TextUtils.isEmpty(quote.id)) {
            if(userId != null) {
                quote.id = db.child(getString(R.string.database_tree_quotes))
                        .child(userId)
                        .child(quote.type)
                        .push()
                        .getKey();

                quote.created = DateUtils.getDateString();

            } else {
                Timber.e(new Exception(), "User was not authenticated before creating quote");
                showErrorSnackbar(R.string.error_database);

                return false;
            }
        }

        // Write data to the database
        String quotePath = "/" + getString(R.string.database_tree_quotes) +
                "/" + userId +
                "/" + quote.type +
                "/" + quote.id;

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(quotePath, quote.toMap());

        db.updateChildren(childUpdates);
        Timber.d("Saved quote object: " + quote.toString());

        return true;
    }

    private void setupTabs() {
        // Restore fragments using tags assigned by the viewpager
        pricingFragment = (QuoteFormPricingFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.view_pager + ":0");

        QuoteFormDealerFragment dealerFragment = (QuoteFormDealerFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.view_pager + ":1");

        if(pricingFragment == null) {
            if (quote.type.equals(getString(R.string.quote_type_loan))) {
                pricingFragment = QuoteFormLoanPricingFragment.newInstance(null);
            } else {
                pricingFragment = QuoteFormLeasePricingFragment.newInstance(null);
            }
        }

        if(dealerFragment == null) {
            dealerFragment = QuoteFormDealerFragment.newInstance(null);
        }

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(pricingFragment);
        fragments.add(dealerFragment);

        QuoteFormPagerAdapter pagerAdapter = new QuoteFormPagerAdapter(getSupportFragmentManager(), this, fragments);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
    }

    private void setupToolbar() {
        toolbarPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleToolbarDisplayMode();
            }
        });
    }

    private void toggleToolbarDisplayMode() {
        Timber.d("Toggle toolbar display mode");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quote_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onPageScrollStateChanged(int state) {}
    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override public void onPageSelected(int position) {
        pricingFragment.hideKeyboard();
    }

    private void showErrorSnackbar(@StringRes int messageId) {
        Snackbar errorSnackbar = Snackbar.make(viewPager, messageId, Snackbar.LENGTH_INDEFINITE);

        errorSnackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        errorSnackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.snackbarBackground));
        errorSnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbarActionText));
        errorSnackbar.show();
    }

    @Override
    public Quote getQuote() {
        return quote;
    }
}

