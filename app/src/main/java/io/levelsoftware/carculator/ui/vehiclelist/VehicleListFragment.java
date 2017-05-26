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

import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.sync.BaseIntentService;
import io.levelsoftware.carculator.sync.SyncBroadcastReceiver;
import io.levelsoftware.carculator.sync.vehicle.VehicleIntentService;
import timber.log.Timber;


public class VehicleListFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        SyncBroadcastReceiver.OnStatusUpdateListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int KEY_VEHICLE_LOADER = 0;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private SyncBroadcastReceiver receiver;
    private VehicleListContainerAdapter adaper;

    private String searchQuery = "";
    private boolean syncError = false;
    private int count;

    public VehicleListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);

        adaper = new VehicleListContainerAdapter();
        recyclerView.setAdapter(adaper);

        if(savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(VehicleListActivity.KEY_SEARCH_QUERY);
        }

        bindSyncReceiver();
        getActivity().getSupportLoaderManager().initLoader(KEY_VEHICLE_LOADER, null, this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBindSyncReceiver();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(VehicleListActivity.KEY_SEARCH_QUERY, searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        loadVehicleData();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder =      CarculatorContract.Vehicle.COLUMN_MAKE_NAME      + " ASC, " +
                                CarculatorContract.Vehicle.COLUMN_YEAR           + " DESC, " +
                                CarculatorContract.Vehicle.COLUMN_NAME           + " ASC";

        return new CursorLoader(getActivity(),
                CarculatorContract.Vehicle.CONTENT_URI,
                null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        count = data.getCount();
        if(count == 0 && !syncError) {
            loadVehicleData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            adaper.setCursor(data);
            adaper.filter(searchQuery);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaper.setCursor(null);
    }

    private void loadVehicleData() {
        syncError = false;
        swipeRefreshLayout.setRefreshing(true);

        if(!VehicleIntentService.isRunning()) {
            VehicleIntentService.start(getActivity());
        }
    }

    private void bindSyncReceiver() {
        receiver = new SyncBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                new IntentFilter(getString(R.string.broadcast_sync_vehicle)));
    }

    private void unBindSyncReceiver() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void statusSuccess() {
        Timber.d("RECEIVED: Status broadcast success");
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void statusError(int code, @Nullable String message) {
        Timber.d("RECEIVED: Status broadcast error");
        syncError = true;
        if(count == 0) {
            showError(code);
        }
    }

    private void showError(int code) {
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

        switch(code) {
            case BaseIntentService.STATUS_ERROR_NO_NETWORK:
                Toast.makeText(getActivity(), "No Network", Toast.LENGTH_LONG).show();
                break;

            case BaseIntentService.STATUS_ERROR_NETWORK_ISSUE:
                Toast.makeText(getActivity(), "Network Problem", Toast.LENGTH_LONG).show();
                break;

            default:
                Toast.makeText(getActivity(), "Error Loading Data", Toast.LENGTH_LONG).show();
        }
    }

    public void filter(@Nullable String query) {
        this.searchQuery = query;
        if(query != null) {
            adaper.filter(query);
        }
    }
}
