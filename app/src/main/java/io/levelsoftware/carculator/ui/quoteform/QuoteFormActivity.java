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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;
import io.levelsoftware.carculator.model.quote.Fee;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.model.quote.Vehicle;
import io.levelsoftware.carculator.ui.vehiclelist.VehicleListActivity;
import io.levelsoftware.carculator.util.UserUtils;
import timber.log.Timber;

public class QuoteFormActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;

    @BindView(R.id.linear_layout_toolbar_price) LinearLayout toolbarPriceLinearLayout;
    @BindView(R.id.text_view_toolbar_price) TextView toolbarPriceTextView;
    @BindView(R.id.text_view_toolbar_price_label) TextView toolbarPriceLabelTextView;

    private QuoteFormPagerAdapter pagerAdapter;
    private QuoteFormFragment formFragment;
    private QuoteFormDealerFragment dealerFragment;

    DatabaseReference db;

    private String userId;
    private String quoteId;
    private String quoteType;

    private Make make;
    private Model model;

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

        db = FirebaseDatabase.getInstance().getReference();
        userId = UserUtils.getInstance().getUid();

        quoteType = getIntent().getStringExtra(getString(R.string.intent_key_quote_type));
        quoteId = getIntent().getStringExtra(getString(R.string.intent_key_quote_id));

        // If we didn't get an existing quote, create a new one
        if(TextUtils.isEmpty(quoteId)) {
            model = getIntent().getParcelableExtra(getString(R.string.intent_key_quote_model));
            make = getIntent().getParcelableExtra(getString(R.string.intent_key_quote_make));
            createQuote();
        }

        setupTabs();
        setupToolbar();
    }

    @Override
    public void onBackPressed() {
        // Close the keyboard on back button press if it is open
        if(formFragment.keyboardVisible()) {
            formFragment.hideKeyboard();
            return;
        }

        saveQuote();
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.removeOnPageChangeListener(this);
    }

    private void createQuote() {
        Timber.d("Attempting to create new quote of type '"+ quoteType +"' for user: " + userId);

        if(userId != null) {
            quoteId = db.child(getString(R.string.database_tree_quotes))
                    .child(userId)
                    .child(quoteType)
                    .push()
                    .getKey();

            Timber.d("Created new quote with id: " + quoteId);
        } else {
            Timber.e(new Exception(), "User was not authenticated before creating quote");

            setResult(VehicleListActivity.RESULT_ERROR);
            finish();
        }
    }

    private void saveQuote() {
        Timber.d("Called save quote");

        String quotePath = "/" + getString(R.string.database_tree_quotes) +
                "/" + userId +
                "/" + quoteType +
                "/" + quoteId;

        Map<String, Object> childUpdates = new HashMap<>();

        Vehicle vehicle = new Vehicle(make, model);
        Quote quote = new Quote(vehicle);

        quote.price = "24569.25";
        quote.fees = new ArrayList<>();
        quote.fees.add(new Fee("Documentation", "25.69", true, true));

        childUpdates.put(quotePath, quote.toMap());

        db.updateChildren(childUpdates);
    }

    private void setupTabs() {
        if(quoteType.equals(getString(R.string.quote_type_lease))) {
            formFragment = QuoteFormLeaseFragment.newInstance(null);
        } else if(quoteType.equals(getString(R.string.quote_type_loan))) {
            formFragment = QuoteFormLoanFragment.newInstance(null);
        }
        dealerFragment = QuoteFormDealerFragment.newInstance(null);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(formFragment);
        fragments.add(dealerFragment);

        pagerAdapter = new QuoteFormPagerAdapter(getSupportFragmentManager(), this, fragments);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
    }

    private void setupToolbar() {
        toolbarPriceLinearLayout.setOnClickListener(new View.OnClickListener() {
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
        formFragment.hideKeyboard();
    }


}

