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

package io.levelsoftware.carculator.ui.quoteform.manager;

import java.util.ArrayList;

import timber.log.Timber;

public class Section {
    private String title;
    private ArrayList<String> fieldKeys = new ArrayList<>();

    public Section(String title) {
        this.title = title;
    }

    protected void addField(Field field) {
        String key = field.getKey();
        fieldKeys.add(key);
    }

    public String getFieldKeyForIndex(int index) {
        try {
            return fieldKeys.get(index);
        } catch (IndexOutOfBoundsException e) {
            Timber.e("Could not read field key at index " + index, e);
        }
        return null;
    }

    public int getFieldCount() {
        return fieldKeys.size();
    }

    public String getTitle() {
        return title;
    }
}
