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

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.ui.quoteform.manager.Field;
import timber.log.Timber;

public class QuoteFormNestedViewHolder extends RecyclerView.ViewHolder
    implements TextWatcher {

    @BindView(R.id.text_input_layout) TextInputLayout textInputLayout;
    @BindView(R.id.edit_text) TextInputEditText editText;

    private Field field;

    public QuoteFormNestedViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

        editText.addTextChangedListener(this);
        editText.setTextIsSelectable(true);
//        editText.setOnFocusChangeListener(this);

//        editText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                ((InputMethodManager)view.getContext().getSystemService(INPUT_METHOD_SERVICE))
//                        .hideSoftInputFromWindow(view.getWindowToken(), 0);
//
//                if(view.equals(editText)) {
//                    editText.requestFocus();
//                }
//
//                return true;
//            }
//        });
    }

    public void setFormField(Field field) {
        this.field = field;
        textInputLayout.setHint(field.getTitle());
        editText.setContentDescription(field.getTitle());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Timber.d("Text changed: " + s);
    }

    @Override
    public void afterTextChanged(Editable s) {}

//    @Override
//    public void onFocusChange(View view, boolean hasFocus) {
//        Timber.d("Edit text " + field.getKey() + " has focus: " + hasFocus);
//
//        if(hasFocus) {
//        }
//
//        if(!hasFocus) {
//
//        }
//    }


}
