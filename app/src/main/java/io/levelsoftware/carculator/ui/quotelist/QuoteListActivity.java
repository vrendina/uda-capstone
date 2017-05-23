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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.sync.VehicleIntentService;
import io.levelsoftware.carculator.sync.VehicleJobService;
import io.levelsoftware.carculator.ui.vehiclelist.VehicleListActivity;
import timber.log.Timber;

public class QuoteListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;

    private String[] tabNames;
    private String[] tabKeys;

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

    private void setupSync() {
        Timber.d("Starting immediate synchronization of vehicle data...");
        VehicleIntentService.start(this);

        Timber.d("Scheduling future syncronization of vehicle data...");
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job vehicleSyncJob = dispatcher.newJobBuilder()
                .setService(VehicleJobService.class)
                .setTag(getString(R.string.job_sync_vehicle))
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(10, 20))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(vehicleSyncJob);
    }

    private void setupTabs() {
        tabNames = getResources().getStringArray(R.array.quote_list_tab_names);
        tabKeys = getResources().getStringArray(R.array.quote_list_tab_keys);

        QuoteListPagerAdapter pagerAdapter = new QuoteListPagerAdapter(getSupportFragmentManager(),
                tabNames, tabKeys);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupFab() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tabIndex = viewPager.getCurrentItem();
                Timber.v("Adding new quote for quote type: " + tabNames[tabIndex]);

                Intent intent = new Intent(QuoteListActivity.this, VehicleListActivity.class);
                intent.putExtra(getString(R.string.key_quote_type), tabKeys[tabIndex]);
                startActivity(intent);
            }
        });
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
