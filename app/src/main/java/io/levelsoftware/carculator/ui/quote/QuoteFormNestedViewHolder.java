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

package io.levelsoftware.carculator.ui.quote;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;

public class QuoteFormNestedViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_input_layout) TextInputLayout textInputLayout;
    @BindView(R.id.edit_text) TextInputEditText editText;

    public QuoteFormNestedViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}
