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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.util.UserUtils;
import timber.log.Timber;


public class QuoteListFragment extends Fragment implements ValueEventListener {

    @BindView(R.id.section_label) TextView sectionLabel;

    private DatabaseReference db;
    private String quoteType;

    public QuoteListFragment() {}

    public static QuoteListFragment newInstance(String quoteType) {
        QuoteListFragment fragment = new QuoteListFragment();
        fragment.quoteType = quoteType;
        fragment.db = FirebaseDatabase.getInstance().getReference();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quote_list, container, false);
        ButterKnife.bind(this, view);

        sectionLabel.setText("Quote type: " + quoteType);

        addDataListener();

        return view;
    }

    private void addDataListener() {
        Timber.d("BINDING DATA LISTENER("+ quoteType +")");
        String uid = UserUtils.getInstance().getUid();
        if(!TextUtils.isEmpty(uid)) {
            db.child(getString(R.string.database_tree_quotes))
                    .child(uid)
                    .child(quoteType)
                    .addValueEventListener(this);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
            Timber.d("Got data ("+ quoteType +"): " + snapshot.getValue().toString());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
