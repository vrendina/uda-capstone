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

package io.levelsoftware.carculator.ui.quoteform;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.ui.quoteform.manager.FormManager;
import io.levelsoftware.carculator.ui.quoteform.manager.Section;
import timber.log.Timber;

public class QuoteFormContainerAdapter extends RecyclerView.Adapter<QuoteFormContainerViewHolder> {

    private FormManager form;

    public QuoteFormContainerAdapter(FormManager form) {
        this.form = form;
    }

    @Override
    public QuoteFormContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.list_item_quote_form_container, parent, false);
        return new QuoteFormContainerViewHolder(view);
    }

    @Override
    public void onViewAttachedToWindow(QuoteFormContainerViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        Timber.d("View attached to window: " + holder.sectionTitleTextView.getText());
    }

    @Override
    public void onViewDetachedFromWindow(QuoteFormContainerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Timber.d("View detached from window:" + holder.sectionTitleTextView.getText());
    }

    @Override
    public void onBindViewHolder(QuoteFormContainerViewHolder holder, int position) {
        Timber.d("Bind section view holder: " + form.getSectionByIndex(position).getTitle());

        Section section = form.getSectionByIndex(position);
        holder.setFormSection(section, form);
    }

    @Override
    public int getItemCount() {
        return form.getSectionCount();
    }
}
