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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.sync.vehicle.VehicleIntentService;
import io.levelsoftware.carculator.ui.quoteform.QuoteFormActivity;
import io.levelsoftware.carculator.ui.vehiclelist.VehicleListActivity;
import timber.log.Timber;

public class QuoteListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;

    private BroadcastReceiver clickReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setupSync();
        setupTabs();
        setupFab();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindClickReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindClickReceiver();
    }

    private void setupSync() {
        Timber.d("Starting immediate synchronization of vehicle data...");
        VehicleIntentService.start(this);

//        Timber.d("Scheduling future syncronization of vehicle data...");
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//
//        Job vehicleSyncJob = dispatcher.newJobBuilder()
//                .setService(VehicleJobService.class)
//                .setTag(getString(R.string.job_sync_vehicle))
//                .setRecurring(true)
//                .setLifetime(Lifetime.FOREVER)
//                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
//                .setReplaceCurrent(true)
//                .setTrigger(Trigger.executionWindow(10, 20))
//                .setConstraints(Constraint.ON_ANY_NETWORK)
//                .build();
//
//        dispatcher.mustSchedule(vehicleSyncJob);
    }

    private void setupTabs() {
        // Restore fragments using tags assigned by the viewpager
        QuoteListFragment loanFragment = (QuoteListFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.view_pager + ":0");

        QuoteListFragment leaseFragment = (QuoteListFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.view_pager + ":1");

        if(loanFragment == null) {
            Bundle loanArgs = new Bundle();
            loanArgs.putString(getString(R.string.intent_key_quote_type), getString(R.string.quote_type_loan));

            loanFragment = QuoteListFragment.newInstance(loanArgs);
        }

        if(leaseFragment == null) {
            Bundle leaseArgs = new Bundle();
            leaseArgs.putString(getString(R.string.intent_key_quote_type), getString(R.string.quote_type_lease));

            leaseFragment = QuoteListFragment.newInstance(leaseArgs);
        }

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(loanFragment);
        fragments.add(leaseFragment);

        QuoteListPagerAdapter pagerAdapter = new QuoteListPagerAdapter(getSupportFragmentManager(),
                this, fragments);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupFab() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuoteListActivity.this, VehicleListActivity.class);
                intent.putExtra(getString(R.string.intent_key_quote_type), getQuoteTypeForTabPosition());

                startActivity(intent);
            }
        });
    }

    private void bindClickReceiver() {
        clickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Quote quote = intent.getParcelableExtra(getString(R.string.intent_key_quote));

                Intent quoteIntent = new Intent(QuoteListActivity.this, QuoteFormActivity.class);
                quoteIntent.putExtra(getString(R.string.intent_key_quote), quote);

                startActivity(quoteIntent);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(clickReceiver,
                new IntentFilter(getString(R.string.broadcast_click_quote)));
    }

    private void unBindClickReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(clickReceiver);
    }

    private String getQuoteTypeForTabPosition() {
        int tabIndex = viewPager.getCurrentItem();

        switch (tabIndex) {
            case 0:
                return getString(R.string.quote_type_loan);
            case 1:
                return getString(R.string.quote_type_lease);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quote_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
