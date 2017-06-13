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
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.data.CarculatorContract;
import io.levelsoftware.carculator.sync.BaseIntentService;
import io.levelsoftware.carculator.sync.SyncBroadcastReceiver;
import io.levelsoftware.carculator.sync.vehicle.VehicleIntentService;
import io.levelsoftware.carculator.util.NetworkUtils;
import io.levelsoftware.carculator.util.ProviderUtils;


public class VehicleListFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        SyncBroadcastReceiver.OnStatusUpdateListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int KEY_VEHICLE_LOADER = 0;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.image_view_status) ImageView statusImageView;

    private SyncBroadcastReceiver syncReceiver;
    private BroadcastReceiver networkReceiver;

    String searchQuery;
    private VehicleListContainerAdapter adaper;

    private Snackbar errorSnackbar;

    public int count;
    private boolean initialLoadComplete = false;

    public VehicleListFragment() {}

    public static VehicleListFragment newInstance(Bundle arguments) {
        VehicleListFragment fragment = new VehicleListFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.primaryDark),
                ContextCompat.getColor(getContext(), R.color.accent),
                ContextCompat.getColor(getContext(), R.color.accent)
        );

        adaper = new VehicleListContainerAdapter();
        recyclerView.setAdapter(adaper);

        showVehicleList(false);
        hideStatusImage();

        bindReceivers();
        getActivity().getSupportLoaderManager().initLoader(KEY_VEHICLE_LOADER, getArguments(), this);

        searchQuery = getArguments().getString(getString(R.string.intent_key_search_query));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindReceivers();
    }

    @Override
    public void onRefresh() {
        loadVehicleData();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String quoteType = args.getString(getString(R.string.intent_key_quote_type),
                getString(R.string.quote_type_lease));

        String sortOrder =      CarculatorContract.Vehicle.COLUMN_MAKE_NAME      + " COLLATE NOCASE ASC, " +
                                CarculatorContract.Vehicle.COLUMN_MODEL_NAME     + " COLLATE NOCASE ASC";

        String selection = null;
        String[] selectionArgs = null;

        // Only show vehicles from last 2 years if creating a lease quote
        if(quoteType.equals(getString(R.string.quote_type_lease))) {
            selection = CarculatorContract.Vehicle.COLUMN_CURRENT_YEAR + " > ?";

            int year = Calendar.getInstance().get(Calendar.YEAR) - 2;
            selectionArgs = new String[]{String.valueOf(year)};
        }

        return new CursorLoader(getActivity(),
                CarculatorContract.Vehicle.CONTENT_URI,
                null, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        count = data.getCount();
        initialLoadComplete = true;

        if(shouldLoadVehicleData()) {
            loadVehicleData();
        }

        // If we are already loading vehicle data, show the spinner
        if(vehicleDataIsLoading()) {
            setSwipeRefreshLayoutState(true, true);
        }

        // If we have data, show the data
        if(count > 0) {
            setSwipeRefreshLayoutState(false, false);

            adaper.setData(ProviderUtils.getMakes(data));
            adaper.filter(searchQuery);

            // For some reason the RecyclerView starts scrolled down slightly
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollBy(0,-Integer.MAX_VALUE);
                }
            });
            showVehicleList(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaper.setData(null);
    }

    private void loadVehicleData() {
        VehicleIntentService.start(getActivity());

        setSwipeRefreshLayoutState(true, true);
        hideError();
    }

    private boolean shouldLoadVehicleData() {
        // If we already have data, we shouldn't try to load any
        if(count > 0) {
            return false;
        }
        // If we haven't finished running the loader to check for existing data
        if(!initialLoadComplete) {
            return false;
        }
        // If the vehicle service is already running don't load
        return !vehicleDataIsLoading();
    }

    private boolean vehicleDataIsLoading() {
        return VehicleIntentService.isRunning();
    }

    private void bindReceivers() {
        syncReceiver = new SyncBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(syncReceiver,
                new IntentFilter(getString(R.string.broadcast_sync_vehicle)));

        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(NetworkUtils.networkIsAvailable(context)) {
                    if(shouldLoadVehicleData()) {
                        loadVehicleData();
                    }
                }
            }
        };
        getContext().registerReceiver(networkReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unbindReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(syncReceiver);
        getContext().unregisterReceiver(networkReceiver);
    }

    @Override
    public void statusSuccess() {
        setSwipeRefreshLayoutState(false, false);
    }

    @Override
    public void statusError(int code, @Nullable String message) {
        if(count == 0) {
            showError(code);
        }
    }

    private void showStatusImage(@DrawableRes int drawable) {
        statusImageView.setImageResource(drawable);
        statusImageView.setVisibility(View.VISIBLE);
    }

    private void hideStatusImage() {
        statusImageView.setVisibility(View.GONE);
    }

    private void showVehicleList(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        recyclerView.setVisibility(visibility);
    }

    private void setSwipeRefreshLayoutState(boolean enabled, boolean refreshing) {
        swipeRefreshLayout.setEnabled(enabled);
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void showError(int code) {
        setSwipeRefreshLayoutState(true, false);
        showVehicleList(false);

        switch(code) {
            case BaseIntentService.STATUS_ERROR_NO_NETWORK:
                showErrorSnackbar(R.string.error_no_network, false);
                showStatusImage(R.drawable.ic_logo_no_network);
                break;

            case BaseIntentService.STATUS_ERROR_NETWORK_ISSUE:
                showErrorSnackbar(R.string.error_network_error, true);
                showStatusImage(R.drawable.ic_logo_no_network);
                break;

            default:
                showErrorSnackbar(R.string.error_data, true);
                showStatusImage(R.drawable.ic_logo_error);
        }
    }

    private void hideError() {
        hideStatusImage();

        if(errorSnackbar != null) {
            errorSnackbar.dismiss();
        }
    }

    private void showErrorSnackbar(@StringRes int messageId, boolean showRetry) {
        View view = getView();
        if(view != null) {
            errorSnackbar = Snackbar.make(view, messageId, Snackbar.LENGTH_INDEFINITE);

            if(showRetry) {
                errorSnackbar.setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadVehicleData();
                    }
                });
            }

            errorSnackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.snackbarBackground));
            errorSnackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.snackbarActionText));
            errorSnackbar.show();
        }
    }

    public void filter(@Nullable String query) {
        if(query != null) {
            if(!query.equals(searchQuery)) {
                adaper.filter(query);

                if (adaper.getItemCount() == 0) {
                    showStatusImage(R.drawable.ic_logo_no_results);
                } else {
                    hideStatusImage();
                }

                searchQuery = query;
            }
        }
    }

}
