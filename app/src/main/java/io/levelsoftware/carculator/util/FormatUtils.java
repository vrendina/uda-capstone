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

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    private static FormatUtils formatUtils;
    private static NumberFormat formatter;

    private FormatUtils() {
        formatter = NumberFormat.getInstance(Locale.getDefault());
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    private static void initializeFormatUtils() {
        if(formatUtils == null) {
            formatUtils = new FormatUtils();
        }
    }

    public static NumberFormat getFormatter() {
        if(formatter == null) {
            initializeFormatUtils();
        }
        return formatter;
    }

}
