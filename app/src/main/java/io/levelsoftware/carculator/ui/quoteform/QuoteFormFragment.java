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

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import butterknife.BindView;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.ui.FormField;
import io.levelsoftware.keyculator.Keyculator;
import timber.log.Timber;


public abstract class QuoteFormFragment extends Fragment
        implements FormField.OnFormFieldEventListener {

    @BindView(R.id.form_container) LinearLayout container;
    @BindView(R.id.keyculator) Keyculator keyculator;
    @BindView(R.id.scroll_view) ScrollView scrollView;

    protected SparseArray<FormField> fields = new SparseArray<>();

    public QuoteFormFragment() {}

    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState);

    protected void collectFields(ViewGroup parent) {
        for(int i = 0; i<parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child instanceof FormField) {
                Timber.d("Found form field " + child.toString());
                fields.put(child.getId(), (FormField) child);
            } else if(child instanceof ViewGroup) {
                collectFields((ViewGroup) child);
            }
        }
    }

    protected void attachListeners() {
        for(int i = 0; i<fields.size(); i++) {
            FormField field = fields.get(fields.keyAt(i));
            field.setOnFormFieldEventListener(this);
        }
    }

    protected void setupFields() {
        collectFields(container);
        attachListeners();
    }

    protected void setupKeyboard() {
        keyculator.setScrollView(scrollView);
    }

    @Override
    public void fieldFocusChanged(@IdRes int id, boolean hasFocus) {
        Timber.d("Focus changed for " + id + " has focus: " + hasFocus);
        if(hasFocus) {
            showKeyboard();

            FormField field = fields.get(id);
            Rect rect = new Rect(0, 0, field.getWidth(), field.getHeight());
            field.requestRectangleOnScreen(rect, false);
        }
    }

    protected void showKeyboard() {
        keyculator.showKeyboard();
    }

    protected void hideKeyboard() {
        ((View)scrollView.getParent()).requestFocus();
        keyculator.hideKeyboard();
    }

}
