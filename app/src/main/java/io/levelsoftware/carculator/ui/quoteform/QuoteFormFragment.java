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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.ui.quoteform.manager.Field;
import io.levelsoftware.carculator.ui.quoteform.manager.FormManager;
import io.levelsoftware.carculator.ui.quoteform.manager.Section;
import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class QuoteFormFragment extends Fragment {

    @BindView(R.id.frame_layout) FrameLayout frameLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private BroadcastReceiver focusBroadcastReceiver;

    public QuoteFormFragment() {}

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quote_form, container, false);
        ButterKnife.bind(this, view);

        FormManager form = buildForm();

        QuoteFormContainerAdapter adapter = new QuoteFormContainerAdapter(form);
        recyclerView.setAdapter(adapter);


        focusBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String fieldKey = intent.getStringExtra(getString(R.string.intent_key_focus_field));

                Timber.d("Got focus broadcast: " + fieldKey);

                // If a field key wasn't passed then close the keyboard and remove all focus
                if(TextUtils.isEmpty(fieldKey)) {
                    closeInput();

                // Otherwise we need to scroll the view so the EditText doesn't get recycled when the keyboard loads
                } else {
                    Timber.d("Called scroll to position");
//                    recyclerView.smoothScrollBy(0, 400);
                    //recyclerView.scrollBy(0, 400);
                }
            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(focusBroadcastReceiver,
                new IntentFilter(getString(R.string.broadcast_focus_field)));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(focusBroadcastReceiver);
    }

    private FormManager buildForm() {
        FormManager form = new FormManager();
        Section costs = form.addSection(new Section("Section 1"));
        Section reductions = form.addSection(new Section("Section 2"));
        Section fees = form.addSection(new Section("Section 3"));
        Section test = form.addSection(new Section("Section 4"));
//        Section moretest = form.addSection(new Section("Section 5"));

        form.addField(costs, new Field.FieldBuilder("test1")
                .title("Test 1")
                .entryType(Field.ENTRY_TYPE_CURRENCY)
                .build());

        form.addField(costs, new Field.FieldBuilder("test2")
                .title("Test 2")
                .entryType(Field.ENTRY_TYPE_CURRENCY)
                .build());

        form.addField(costs, new Field.FieldBuilder("test3")
                .title("Test 3")
                .entryType(Field.ENTRY_TYPE_CURRENCY)
                .build());

        form.addField(reductions, new Field.FieldBuilder("test4")
                .title("Test 4")
                .entryType(Field.ENTRY_TYPE_CURRENCY)
                .build());

        for(int i = 5; i<25; i++) {
            form.addField(fees, new Field.FieldBuilder("test" + i)
                    .title("Test " + i)
                    .entryType(Field.ENTRY_TYPE_CURRENCY)
                    .build());
        }

        for(int i = 25; i<75; i++) {
            form.addField(test, new Field.FieldBuilder("test" + i)
                    .title("Test " + i)
                    .entryType(Field.ENTRY_TYPE_CURRENCY)
                    .build());
        }

        return form;
    }

    public void closeInput() {
        ((InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getView().getWindowToken(), 0);

        frameLayout.requestFocus();
    }

    public void openInput() {

    }

}
