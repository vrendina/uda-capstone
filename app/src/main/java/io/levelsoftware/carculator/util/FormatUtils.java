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

package io.levelsoftware.carculator.util;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    private static NumberFormat formatter;

    static {
        formatter = NumberFormat.getInstance(Locale.getDefault());
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    public static NumberFormat getFormatter() {
        return formatter;
    }

    /**
     * Format the given number as currency. If the number is less than $1000 then it
     * will be formatted with 2 decimal places.
     *
     * @param number to format
     * @return string number formatted as currency
     */
    public static String formatCurrency(@NonNull BigDecimal number) {
        NumberFormat formatter = FormatUtils.getFormatter();
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        if(number.compareTo(new BigDecimal("1000")) == -1
                && number.compareTo(new BigDecimal("-1000")) == 1) {
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
        }

        return "$" + formatter.format(number);
    }

}
