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

public class Field {

    public static final int ENTRY_TYPE_NUMBER = 0;
    public static final int ENTRY_TYPE_DECIMAL = 1;
    public static final int ENTRY_TYPE_CURRENCY = 2;
    public static final int ENTRY_TYPE_TEXT = 3;

    private final String key;
    private final String title;
    private final int entryType;

    private Field(FieldBuilder builder) {
        this.key = builder.key;
        this.title = builder.title;
        this.entryType = builder.entryType;
    }

    @Override
    public String toString() {
        return "Field{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", entryType=" + entryType +
                '}';
    }

    public String getKey() {
        return key;
    }

    public String getTitle() { return title; }

    public static class FieldBuilder {

        private int entryType;
        private String key;
        private String title;

        public FieldBuilder(String key) {
            this.key = key;
        }

        public FieldBuilder title(String title) {
            this.title = title;
            return this;
        }

        public FieldBuilder entryType(int entryType) {
            this.entryType = entryType;
            return this;
        }

        public Field build() {
            return new Field(this);
        }
    }
}
