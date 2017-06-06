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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

public class ValueManager {

    protected ArrayList<Operand> operands = new ArrayList<>(2);
    protected String operator;

    protected static final String OPERATOR_ADDITION = "+";
    protected static final String OPERATOR_SUBTRACTION = "−";
    protected static final String OPERATOR_DIVISION = "÷";
    protected static final String OPERATOR_MULTIPLICATION = "×";

    protected static ArrayList<String> operators = new ArrayList<>(4);

    private NumberFormat formatter = NumberFormat.getInstance(Locale.US);

    static {
        operators.add(OPERATOR_ADDITION);
        operators.add(OPERATOR_SUBTRACTION);
        operators.add(OPERATOR_DIVISION);
        operators.add(OPERATOR_MULTIPLICATION);
    }

    protected ValueManager() {
        operands.add(new Operand());
        operands.add(new Operand());
    }

    protected String getFormattedString() {
        return operands.get(0).getFormattedValue() +
                " " +
                ((operator == null) ? "" : operator) +
                " " +
                operands.get(1).getFormattedValue();
    }

    protected void append(String value) {
        if(isOperator(value)) {
            // If this is the first character don't add an operator
            if(operands.get(0).charSequence.size() == 0) {
                return;
            }

            // We already have an operator, evaluate the pending operation
            if(operator != null && operands.get(1).charSequence.size() > 0) {
                // run evaluation
                return;
            }
            operator = value;
            return;
        }

        /*
         * If the operator isn't set then add to the first operand, otherwise add to the second.
         */
        if(operator == null) {
            operands.get(0).addCharacter(value);
        } else {
            operands.get(1).addCharacter(value);
        }
    }

    protected void removeLast() {
        if(operands.get(1).removeLastCharacter()) {
            return;
        }

        if(operator != null) {
            operator = null;
            return;
        }

        operands.get(0).removeLastCharacter();
    }

    protected void clear() {
        operands.get(0).removeAllCharacters();
        operands.get(1).removeAllCharacters();
        operator = null;
    }

    protected BigDecimal evaluate() {
        BigDecimal x = operands.get(0).getDecimalValue();
        BigDecimal y = operands.get(1).getDecimalValue();

        return x;
    }

    private boolean isOperator(String value) {
        return operators.contains(value);
    }

    protected class Operand {

        private String formattedValue;
        private String stringValue;
        private BigDecimal decimalValue;
        private ArrayList<String> charSequence = new ArrayList<>();

        private Long characteristic;
        private Long mantissa;

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
                            + ((getMantissa() == null) ? "" : getMantissa().toString());

                    Timber.v("Initial value: " + getStringValue() + " Formatted value: " + formattedValue);
                }
            }
            return formattedValue;
        }

        protected BigDecimal getDecimalValue() {
            if(decimalValue == null) {
                decimalValue = new BigDecimal(getStringValue());
            }
            return decimalValue;
        }

        protected Long getCharacteristic() {
            if(characteristic == null) {
                String[] components = getStringValue().split("\\.");
                if(components.length > 0 && !TextUtils.isEmpty(components[0])) {
                    characteristic = Long.parseLong(components[0]);
                }

                if(components.length > 1 && !TextUtils.isEmpty(components[1])) {
                    mantissa = Long.parseLong(components[1]);
                }
            }
            return characteristic;
        }

        protected Long getMantissa() {
            if(mantissa == null) {
                getCharacteristic();
            }
            return mantissa;
        }

        protected String getStringValue() {
            if(stringValue == null) {
                StringBuilder builder = new StringBuilder();
                for (String c : charSequence) {
                    builder.append(c);
                }
                stringValue = builder.toString();
            }
            return stringValue;
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

    @Override
    public String toString() {
        return "ValueManager{" +
                "operands=" + operands +
                ", operator='" + operator + "'" +
                "}";
    }
}
