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

package io.levelsoftware.carculator.ui.vehiclelist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import timber.log.Timber;

public class VehicleListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    private VehicleListFragment listFragment;
    private BroadcastReceiver clickReceiver;

    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        listFragment = (VehicleListFragment) fragmentManager.findFragmentById(R.id.fragment_vehicle_list);

        if(savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(getString(R.string.intent_key_search_query));
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.intent_key_search_query), searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vehicle_list, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.search_hint));

        if(!TextUtils.isEmpty(searchQuery)) {
            searchMenuItem.expandActionView();
            searchView.setQuery(searchQuery, true);
            searchView.requestFocus();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                setSearchQuery(newText);
                return true;
            }
        });

        return true;
    }

    private void bindClickReceiver() {
        clickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Got click broadcast!");
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(clickReceiver,
                new IntentFilter(getString(R.string.broadcast_click_vehicle)));
    }

    private void unBindClickReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(clickReceiver);
    }

    private void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
        listFragment.filter(searchQuery);
    }
}
