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
import java.util.HashMap;

import timber.log.Timber;

public class FormManager {
    private ArrayList<Section> sections = new ArrayList<>();
    private HashMap<String, Field> fields = new HashMap<>();

    public Section addSection(Section section) {
        sections.add(section);
        return section;
    }

    public Field addField(Section section, Field field) {
        if(!sections.contains(section)) {
            sections.add(section);
        }
        fields.put(field.getKey(), field);
        section.addField(field);
        return field;
    }

    public Section getSectionByIndex(int index) {
        try {
            return sections.get(index);
        } catch (IndexOutOfBoundsException e) {
            Timber.e("Could not read section at index " + index, e);
        }

        return null;
    }

    public int getSectionCount() {
        return sections.size();
    }

    public Field getFieldByKey(String key) {
        return fields.get(key);
    }
}
