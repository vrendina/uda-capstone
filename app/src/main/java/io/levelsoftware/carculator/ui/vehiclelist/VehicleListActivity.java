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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Vehicle;
import io.levelsoftware.carculator.ui.quoteform.QuoteFormActivity;
import io.levelsoftware.carculator.util.KeyboardUtils;
import io.levelsoftware.carculator.util.UserUtils;
import timber.log.Timber;

public class VehicleListActivity extends AppCompatActivity {

    public static final int REQUEST_VEHICLE_LIST = 100;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container) FrameLayout container;
    @BindView(R.id.image_view_search) ImageView searchImageView;
    @BindView(R.id.edit_text_search) EditText searchEditText;

    private VehicleListFragment listFragment;
    private BroadcastReceiver clickReceiver;

    private String searchQuery;
    private String quoteType;

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

        quoteType = getIntent().getStringExtra(getString(R.string.intent_key_quote_type));
        if(quoteType == null) {
            quoteType = getString(R.string.quote_type_lease);
        }

        if(savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(getString(R.string.intent_key_search_query));
        }

        UserUtils.getInstance().signInAnonymously();
        Timber.v("User logged in: " + UserUtils.getInstance().getUid());

        setupFragment();
        setupSearch();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindClickReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_VEHICLE_LIST) {
            switch(resultCode) {
                case Activity.RESULT_OK:
                    finish();
                    break;

                default:
                    break;
            }
        }
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

    private void bindClickReceiver() {
        clickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Vehicle vehicle = intent.getParcelableExtra(getString(R.string.intent_key_quote_vehicle));

                Intent quoteIntent = new Intent(VehicleListActivity.this, QuoteFormActivity.class);

                quoteIntent.putExtra(getString(R.string.intent_key_quote_type), quoteType);
                quoteIntent.putExtra(getString(R.string.intent_key_quote_vehicle), vehicle);

                startActivityForResult(quoteIntent, REQUEST_VEHICLE_LIST);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(clickReceiver,
                new IntentFilter(getString(R.string.broadcast_click_vehicle)));
    }

    private void unBindClickReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(clickReceiver);
    }

    private void setupFragment() {
        Bundle arguments = new Bundle();
        arguments.putString(getString(R.string.intent_key_quote_type), quoteType);
        arguments.putString(getString(R.string.intent_key_search_query), searchQuery);

        listFragment = VehicleListFragment.newInstance(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, listFragment).commit();
    }

    private void setupSearch() {
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchEditText.getText().length() == 0) {
                    searchEditText.requestFocus();
                    KeyboardUtils.showSoftKeyboard(getApplicationContext());
                }

                if(searchEditText.getText().length() > 0) {
                    searchEditText.setText("");
                }
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchEditText.clearFocus();
                    KeyboardUtils.closeSoftKeyboard(getApplicationContext(), searchEditText);
                    return true;
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                listFragment.filter(searchQuery);
                updateSearchState();
            }
        });

        updateSearchState();
    }

    private void updateSearchState() {
        if(TextUtils.isEmpty(searchQuery)) {
            searchImageView.setImageResource(R.drawable.ic_search_gray_24dp);
        } else {
            searchImageView.setImageResource(R.drawable.ic_close_gray_24dp);
        }
    }
}
