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


public class NumberFormField extends FrameLayout
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

    public NumberFormField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberFormField);

        hint = typedArray.getString(R.styleable.NumberFormField_hint);
        inputType = typedArray.getInt(R.styleable.NumberFormField_inputType, INPUT_TYPE_INTEGER);
        max = typedArray.getFloat(R.styleable.NumberFormField_max, 1000000);
        min = typedArray.getFloat(R.styleable.NumberFormField_min, 0);

        if(flagIsSet(INPUT_TYPE_INTEGER)) {
            decimals = 0;
        } else {
            decimals = typedArray.getInt(R.styleable.NumberFormField_decimals, MAX_DECIMAL_PLACES);
            if(flagIsSet(INPUT_TYPE_CURRENCY) && decimals == MAX_DECIMAL_PLACES) {
                decimals = getResources().getInteger(R.integer.default_currency_decimals);
            }
        }

        typedArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_number_form_field, this);

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
    public void setRawValue(@NonNull StringNumber rawValue) {
        this.rawValue = rawValue;
        this.value = validate(rawValue);

        Timber.d("Set rawValue (raw): " + rawValue.toString());
        Timber.d("Set rawValue (valid): " + value.toString());

        listener.fieldValueChanged(getId(), value);

        updateDisplay();
    }

    /**
     * Set the initial value to be displayed in the form field. This method skips any animations
     * and other fanciness so the value can be set before the view is displayed. Also does not
     * notify listeners that the field value has changed.
     *
     * @param value value to set the field to, already validated
     */
    public void setInitialValue(@NonNull StringNumber value) {
        this.rawValue = value;
        this.value = value;

        textInputLayout.setHintAnimationEnabled(false);
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setText(getFormattedValue());
                textInputLayout.setHintAnimationEnabled(true);
            }
        });
    }

    @Nullable
    public StringNumber getValue() {
        return value;
    }

    private void updateDisplay() {
        formattedValue = null;

        editText.setText(null);
        editText.append(getFormattedValue());

        setCursorPosition();

        Timber.d("Called update display with value of: " + getFormattedValue());
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

            BigDecimal rounded = decimal.setScale(decimals, BigDecimal.ROUND_HALF_EVEN);

            // If there is a mantissa and it is non-zero don't strip the zeros for currency
            if(flagIsSet(INPUT_TYPE_CURRENCY)) {
                BigInteger mantissa = value.getIntegerMantissa();

                if(mantissa == null) {
                    rounded = rounded.stripTrailingZeros();
                } else if (mantissa.compareTo(BigInteger.ZERO) == 0) {
                    rounded = rounded.stripTrailingZeros();
                }
            } else {
                rounded = rounded.stripTrailingZeros();
            }

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

        // If we lose focus, update the display to reflect the validated value since we are done editing
        } else {
            updateDisplay();
        }
    }

    private String getFormattedValue() {
        if(TextUtils.isEmpty(formattedValue)) {
            if(TextUtils.isEmpty(value.getStringValue())) {
                formattedValue = "";
            } else {
                // Format the field based on the flags
                NumberFormat formatter = FormatUtils.getFormatter();
                formatter.setMaximumFractionDigits(0);
                formatter.setMinimumFractionDigits(0);

                StringBuilder builder = new StringBuilder();
                builder.append(formatter.format(value.getIntegerCharacteristic()));

                if(flagIsSet(INPUT_TYPE_DECIMAL)) {
                    // Show the currency symbol
                    if (flagIsSet(INPUT_TYPE_CURRENCY)) {
                        String currencySymbol = getResources().getString(R.string.currency_symbol);
                        builder.insert(0, " ");
                        builder.insert(0, currencySymbol);
                    }

                    // Show the decimal point if we typed one but haven't added any other digits
                    if (rawValue.hasDecimalPoint()) {
                        builder.append(".");
                    }

                    // Show the value of the mantissa
                    String mantissa = rawValue.getMantissa();
                    if(mantissa != null) {
                        int index = (decimals > mantissa.length()) ? mantissa.length() : decimals;
                        builder.append(mantissa.substring(0, index));
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
        void fieldValueChanged(@IdRes int id, StringNumber value);
    }

}
