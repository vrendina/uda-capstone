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

package io.levelsoftware.keyculator;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Operand {

    private String formattedValue;
    private String stringValue;
    private BigDecimal decimalValue;
    private ArrayList<String> charSequence = new ArrayList<>();

    private NumberFormat formatter = NumberFormat.getInstance(Locale.US);

    private BigInteger characteristic;
    private String mantissa;

    protected void setValue(@NonNull String value) {
        removeAllCharacters();
        for(int i = 0; i < value.length(); i++) {
            addCharacter(String.valueOf(value.charAt(i)));
        }
    }

    protected void addCharacter(String character) {
        if(".".equals(character)) {
            // Don't allow double decimal insertion
            if(charSequence.contains(".")) {
                return;
            }

            // Pad with a zero if putting a decimal first
            if(charSequence.size() == 0) {
                charSequence.add("0");
            }
        }
        charSequence.add(character);
        invalidate();
    }

    protected boolean removeLastCharacter() {
        if(charSequence.size() > 0) {
            charSequence.remove(charSequence.size() - 1);
            invalidate();
            return true;
        }
        return false;
    }

    protected void removeAllCharacters() {
        charSequence.clear();
        invalidate();
    }

    protected String getFormattedValue() {
        if(formattedValue == null) {
            if(charSequence.size() == 0) {
                formattedValue = "";
            } else {
                String decimal = "";
                if(charSequence.contains(".")) {
                    decimal = ".";
                }

                formattedValue = formatter.format(getCharacteristic()) + decimal
                        + ((getMantissa() == null) ? "" : getMantissa());
            }
        }
        return formattedValue;
    }

    protected BigDecimal getDecimalValue() {
        if(decimalValue == null) {
            if(getStringValue() != null) {
                decimalValue = new BigDecimal(getStringValue());
            }
        }
        return decimalValue;
    }

    protected BigInteger getCharacteristic() {
        if(characteristic == null) {
            String[] components = getStringValue().split("\\.");
            if(components.length > 0 && !TextUtils.isEmpty(components[0])) {
                characteristic = new BigInteger(components[0]);
            }

            if(components.length > 1 && !TextUtils.isEmpty(components[1])) {
                mantissa = components[1];
            }
        }
        return characteristic;
    }

    protected String getMantissa() {
        if(mantissa == null) {
            getCharacteristic();
        }
        return mantissa;
    }

    protected String getStringValue() {
        if(stringValue == null) {
            if(charSequence.size() == 0) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            for (String c : charSequence) {
                builder.append(c);
            }
            stringValue = builder.toString();
        }
        return stringValue;
    }

    protected int getLength() {
        return charSequence.size();
    }

    private void invalidate() {
        formattedValue = null;
        decimalValue = null;
        stringValue = null;
        characteristic = null;
        mantissa = null;
    }

    @Override
    public String toString() {
        return getFormattedValue();
    }
}
