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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.model.quote.Quote;
import io.levelsoftware.carculator.util.UserUtils;
import timber.log.Timber;


public class QuoteListFragment extends Fragment implements ValueEventListener {

    @BindView(R.id.container_status) LinearLayout statusContainer;
    @BindView(R.id.text_view_status) TextView statusTextView;
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

        setupDataListener(true);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.primaryDark),
                ContextCompat.getColor(getContext(), R.color.accent),
                ContextCompat.getColor(getContext(), R.color.accent)
        );

        String readableQuoteType;
        if(quoteType.equals(getString(R.string.quote_type_lease))) {
            readableQuoteType = getString(R.string.status_quote_lease);
        } else {
            readableQuoteType = getString(R.string.status_quote_loan);
        }

        String statusText = getString(R.string.status_quote_list, readableQuoteType);
        statusTextView.setHint(statusText);

        adapter = new QuoteListContainerAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        setupDataListener(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        //setupDataListener(false);
    }

    private void setupDataListener(boolean attach) {
        String uid = UserUtils.getInstance().getUid();

        if(!TextUtils.isEmpty(uid)) {
            Query query = db.child(getString(R.string.database_tree_quotes))
                                            .child(uid)
                                            .child(quoteType)
                                            .orderByChild("created");

            if(attach && !listenerAttached) {
                Timber.d("Attached value event listener for: " + quoteType);
                listenerAttached = true;
                query.addValueEventListener(this);
            } else {
                if(listenerAttached) {
                    Timber.d("Removing value event listener for: " + quoteType);
                    query.removeEventListener(this);
                    listenerAttached = false;
                }
            }
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Timber.d("Called onDataChange for " + quoteType);

        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
            Quote quote = snapshot.getValue(Quote.class);

            Timber.d("Deserialized quote: " + quote.toString());

        }
    }

    @Override public void onCancelled(DatabaseError databaseError) {}
}
