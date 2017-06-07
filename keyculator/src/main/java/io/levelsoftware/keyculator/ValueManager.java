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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class ValueManager {

    protected static final String OPERATOR_ADDITION = "+";
    protected static final String OPERATOR_SUBTRACTION = "−";
    protected static final String OPERATOR_DIVISION = "÷";
    protected static final String OPERATOR_MULTIPLICATION = "×";

    protected static ArrayList<String> operators = new ArrayList<>(4);

    static {
        operators.add(OPERATOR_ADDITION);
        operators.add(OPERATOR_SUBTRACTION);
        operators.add(OPERATOR_DIVISION);
        operators.add(OPERATOR_MULTIPLICATION);
    }

    private ArrayList<Operand> operands = new ArrayList<>(2);
    private String operator;

    private static final String STATE_KEY_FIRST_OPERAND = "first_operand";
    private static final String STATE_KEY_SECOND_OPERAND = "second_operand";

    private static final String STATE_KEY_OPERATOR = "operator";

    private BigDecimal result;

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
            if(operands.get(0).getLength() == 0) {
                return;
            }

            // We already have an operator and are trying to add another one
            if(operator != null && operands.get(1).getLength() > 0) {
                if(getResult() != null) {
                    setInitialValue(getResult().toPlainString());
                }
            }
            operator = value;
            return;
        }

        /*
         * If the operator isn't set then add to the first operand, otherwise add to the second.
         */
        if(operator == null) {
            operands.get(0).addCharacter(value);
            invalidateResult();
        } else {
            operands.get(1).addCharacter(value);
            invalidateResult();
        }
    }

    protected void removeLast() {
        if(operands.get(1).removeLastCharacter()) {
            invalidateResult();
            return;
        }

        if(operator != null) {
            operator = null;
            return;
        }

        operands.get(0).removeLastCharacter();
        invalidateResult();
    }

    protected void clear() {
        operands.get(0).removeAllCharacters();
        operands.get(1).removeAllCharacters();
        operator = null;
        invalidateResult();
    }

    @Nullable
    protected BigDecimal getResult() {
        if(result == null) {
            result = recalculate();
        }
        return result;
    }

    protected void setInitialValue(@Nullable String value) {
        if(TextUtils.isEmpty(value)) {
            operands.get(0).removeAllCharacters();
        } else {
            operands.get(0).setValue(value);
        }
        operator = null;
        operands.get(1).removeAllCharacters();
    }

    protected Bundle saveState() {
        Bundle outState = new Bundle();

        outState.putString(STATE_KEY_FIRST_OPERAND, operands.get(0).getStringValue());
        outState.putString(STATE_KEY_SECOND_OPERAND, operands.get(1).getStringValue());
        outState.putString(STATE_KEY_OPERATOR, operator);

        return outState;
    }

    protected void restoreState(Bundle bundle) {
        String firstOperand = bundle.getString(STATE_KEY_FIRST_OPERAND);
        String secondOperand = bundle.getString(STATE_KEY_SECOND_OPERAND);

        if(firstOperand != null) {
            operands.get(0).setValue(firstOperand);
        }

        if(secondOperand != null) {
            operands.get(1).setValue(secondOperand);
        }

        operator = bundle.getString(STATE_KEY_OPERATOR);
    }

    private BigDecimal recalculate() {
        BigDecimal x = operands.get(0).getDecimalValue();
        BigDecimal y = operands.get(1).getDecimalValue();

        if(x == null) {
            return null;
        }

        if(operator == null || y == null) {
            return x;
        }

        if(operator.equals(OPERATOR_DIVISION)) {
            if(y.equals(BigDecimal.ZERO)) {
                return x;
            }
            return x.divide(y, 32, BigDecimal.ROUND_HALF_EVEN).setScale(16, RoundingMode.HALF_EVEN).stripTrailingZeros();
        }

        if(operator.equals(OPERATOR_ADDITION)) {
            return x.add(y).stripTrailingZeros();
        }

        if(operator.equals(OPERATOR_SUBTRACTION)) {
            return x.subtract(y).stripTrailingZeros();
        }

        if(operator.equals(OPERATOR_MULTIPLICATION)) {
            return x.multiply(y).setScale(12, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();
        }

        return null;
    }

    private void invalidateResult() {
        result = null;
    }
    private boolean isOperator(String value) {
        return operators.contains(value);
    }
}
