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

package io.levelsoftware.carculator.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import timber.log.Timber;


public class FormFieldView extends FrameLayout
    implements View.OnFocusChangeListener, TextWatcher {

    @BindView(R.id.text_input_layout) TextInputLayout textInputLayout;
    @BindView(R.id.edit_text) TextInputEditText editText;
    @BindView(R.id.image_view_settings) ImageView settingsImageView;

    public static final int INPUT_TYPE_DECIMAL = 0;
    public static final int INPUT_TYPE_INTEGER = 1;
    public static final int INPUT_TYPE_CURRENCY = 2;

    private int inputType;
    private String hint;

    public FormFieldView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FormField);
        hint = typedArray.getString(R.styleable.FormField_hint);
        inputType = typedArray.getInt(R.styleable.FormField_inputType, INPUT_TYPE_DECIMAL);
        typedArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_form_field, this);

        ButterKnife.bind(this, view);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textInputLayout.setHint(hint);

        editText.setTextIsSelectable(true);
        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        Timber.d("Focus changed for " + getId() + " has focus: " + hasFocus);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Timber.d("Text changed " + s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
