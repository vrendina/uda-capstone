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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.util.UserUtils;
import timber.log.Timber;


public class QuoteListFragment extends Fragment implements ValueEventListener {

    @BindView(R.id.image_view_status) ImageView statusImageView;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private QuoteListContainerAdapter adapter;

    private DatabaseReference db;
    private String quoteType;

    private boolean listenerAttached;

    public QuoteListFragment() {}

    public static QuoteListFragment newInstance(Bundle arguments) {
        QuoteListFragment fragment = new QuoteListFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quote_list, container, false);
        ButterKnife.bind(this, view);

        db = FirebaseDatabase.getInstance().getReference();
        quoteType = getArguments().getString(getString(R.string.intent_key_quote_type));

        setupDataListener();

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.primaryDark),
                ContextCompat.getColor(getContext(), R.color.accent),
                ContextCompat.getColor(getContext(), R.color.accent)
        );

        adapter = new QuoteListContainerAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // If the user isn't logged in the first time we load up then the listener doesn't get set properly
        setupDataListener();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setupDataListener() {
        String uid = UserUtils.getInstance().getUid();

        if(!TextUtils.isEmpty(uid)) {
            Query query = db.child(getString(R.string.database_tree_quotes))
                                            .child(uid)
                                            .child(quoteType)
                                            .orderByChild("created");

            // Only attach the listener if it hasn't been attached yet
            if(!listenerAttached) {
                Timber.d("Attached value event listener for: " + quoteType);
                listenerAttached = true;
                query.addValueEventListener(this);
            }
        }
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        Timber.d("Called onDataChange for " + quoteType);

        HashMap<String, ArrayList<Quote>> data = new LinkedHashMap<>();

        // Reverse the order of the quotes so they are sorted newest->oldest
        ArrayList<Quote> reversedQuotes = new ArrayList<>();
        for(DataSnapshot s: snapshot.getChildren()) {
            Quote quote = s.getValue(Quote.class);
            quote.id = s.getKey();
            quote.type = quoteType;

            reversedQuotes.add(0, quote);
        }

        for(Quote quote: reversedQuotes) {
            if(!data.containsKey(quote.vehicle.model.id)) {
                data.put(quote.vehicle.model.id, new ArrayList<Quote>());
            }

            ArrayList<Quote> quotes = data.get(quote.vehicle.model.id);

            // Sort by lowest total cost to highest
            if(quotes.size() > 0) {
                BigDecimal lastTotal = new BigDecimal(quotes.get(0).totalCost);

                if(lastTotal.compareTo(new BigDecimal(quote.totalCost)) == 1) {
                    quotes.add(0, quote);
                } else {
                    quotes.add(quote);
                }

            } else {
                quotes.add(quote);
            }
        }

        if(data.size() > 0) {
            statusImageView.setVisibility(View.GONE);
        } else {
            statusImageView.setVisibility(View.VISIBLE);
        }

        adapter.setData(data);
        recyclerView.smoothScrollToPosition(0);
    }

    @Override public void onCancelled(DatabaseError databaseError) {}
}
