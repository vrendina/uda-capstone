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
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.util.FormatUtils;
import io.levelsoftware.keyculator.StringNumber;
import timber.log.Timber;


public class FormField extends FrameLayout
    implements View.OnFocusChangeListener {

    @BindView(R.id.text_input_layout) TextInputLayout textInputLayout;
    @BindView(R.id.edit_text) TextInputEditText editText;
    @BindView(R.id.image_view_settings) ImageView settingsImageView;

    public static final int INPUT_TYPE_INTEGER = 2;
    public static final int INPUT_TYPE_DECIMAL = 4;
    public static final int INPUT_TYPE_CURRENCY = 8;
    public static final int INPUT_TYPE_PERCENTAGE = 16;

    private static final int MAX_DECIMAL_PLACES = 16;

    private OnFormFieldEventListener listener;

    private int inputType;
    private String hint;
    private float max;
    private float min;
    private int decimals;

    private StringNumber value;
    private StringNumber rawValue;

    private String formattedValue;

    public FormField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FormField);

        hint = typedArray.getString(R.styleable.FormField_hint);
        inputType = typedArray.getInt(R.styleable.FormField_inputType, INPUT_TYPE_INTEGER);
        max = typedArray.getFloat(R.styleable.FormField_max, 1000000);
        min = typedArray.getFloat(R.styleable.FormField_min, 0);

        if(flagIsSet(INPUT_TYPE_INTEGER)) {
            decimals = 0;
        } else {
            decimals = typedArray.getInt(R.styleable.FormField_decimals, MAX_DECIMAL_PLACES);
            if(flagIsSet(INPUT_TYPE_CURRENCY) && decimals == MAX_DECIMAL_PLACES) {
                decimals = getResources().getInteger(R.integer.default_currency_decimals);
            }
        }

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
    }

    /**
     * Set the value of the text field to the supplied value. The value will be formatted to ensure
     * that it is valid for the parameters of this field.
     *
     * @param rawValue value to set before going through validation
     */
    public void setValue(@NonNull StringNumber rawValue) {
        Timber.d("Called setValue with: " + rawValue);
        this.rawValue = rawValue;
        this.value = validate(rawValue);
        Timber.d("Got validated value of: " + value);

        formattedValue = null;
        editText.setText(null);
        editText.append(getFormattedValue());
        setCursorPosition();
    }

    @Nullable
    public StringNumber getValue() {
        return value;
    }

    private void setCursorPosition() {
        int length = editText.getText().length();
        if(flagIsSet(INPUT_TYPE_PERCENTAGE)) {
            editText.setSelection((length - 2 < 0) ? 0 : length - 2);
        } else {
            editText.setSelection(length);
        }
    }

    private StringNumber validate(StringNumber value) {
        BigDecimal decimal = value.getDecimalValue();
        if(decimal != null) {

            BigDecimal minimum = new BigDecimal(min).stripTrailingZeros();
            if(decimal.compareTo(minimum) == -1) {
                return new StringNumber(minimum);
            }

            BigDecimal maximum = new BigDecimal(max).stripTrailingZeros();
            if(decimal.compareTo(maximum) == 1) {
                return new StringNumber(maximum);
            }

            BigDecimal rounded = decimal.setScale(decimals, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();

            if(rounded.compareTo(BigDecimal.ZERO) == 0) {
                return new StringNumber("0");
            } else {
                return new StringNumber(rounded);
            }
        }
        return value;
    }

    public void setOnFormFieldEventListener(OnFormFieldEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(listener != null) {
            listener.fieldFocusChanged(getId(), hasFocus);
        }

        // If focusing on the field set proper cursor position
        if(hasFocus) {
            editText.post(new Runnable() {
                @Override
                public void run() {
                    setCursorPosition();
                }
            });
        }
    }

    private String getFormattedValue() {
        if(TextUtils.isEmpty(formattedValue)) {
            if(TextUtils.isEmpty(value.getStringValue())) {
                formattedValue = "";
            } else {
                // Format the field based on the flags
                NumberFormat formatter = FormatUtils.getFormatter();
                formatter.setMaximumFractionDigits(decimals);

                StringBuilder builder = new StringBuilder();
                builder.append(formatter.format(value.getDecimalValue()));

                if(flagIsSet(INPUT_TYPE_DECIMAL)) {
                    // Show the currency symbol
                    if (flagIsSet(INPUT_TYPE_CURRENCY)) {
                        String currencySymbol = getResources().getString(R.string.currency_symbol);
                        builder.insert(0, " ");
                        builder.insert(0, currencySymbol);
                    }

                    // Show the decimal point if we typed one but haven't added any other digits
                    if (rawValue.hasDecimalPoint() && rawValue.getMantissa() == null) {
                        builder.append(".");
                    }

                    // Append trailing zeros if we have them
                    BigInteger mantissa = rawValue.getIntegerMantissa();
                    if (mantissa != null && mantissa.compareTo(BigInteger.ZERO) == 0) {
                        builder.append(".");
                        builder.append(rawValue.getMantissa());
                    }

                    // Show the percent sign at the end
                    if (flagIsSet(INPUT_TYPE_PERCENTAGE)) {
                        builder.append(" %");
                    }
                }

                formattedValue = builder.toString();
            }
        }
        return formattedValue;
    }

    private boolean flagIsSet(int flag) {
        return (inputType|flag) == inputType;
    }

    public interface OnFormFieldEventListener {
        void fieldFocusChanged(@IdRes int id, boolean hasFocus);
    }

}
