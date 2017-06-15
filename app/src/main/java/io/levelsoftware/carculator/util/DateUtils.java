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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    /**
     * Get the current date formatted as a human readable string.
     *
     * @return String in format yyyy-MM-dd HH:mm:ss
     */
    public static String getDateString() {
        return getFormatter().format(new Date());
    }

    /**
     *  Converts a human readable date string into a date object.
     *
     * @param date string of date in format yyyy-MM-dd HH:mm:ss
     * @return date object from parsed string
     * @throws ParseException if string can not be processed
     */
    public static Date getDateFromString(String date) throws ParseException {
        return getFormatter().parse(date);
    }

    private static SimpleDateFormat getFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

}
