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

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.levelsoftware.carculator.R;

public class QuotePagerAdapter extends FragmentPagerAdapter {

    private String[] pages;
    private String[] keys;

    Context context;

    public QuotePagerAdapter(FragmentManager manager, Context context) {
        super(manager);

        this.context = context;
        this.pages = context.getResources().getStringArray(R.array.quote_entry_tab_names);
        this.keys = context.getResources().getStringArray(R.array.quote_entry_tab_keys);
    }


    @Override
    public Fragment getItem(int position) {
        String key = keys[position];

        if(key.equals(context.getString(R.string.tab_key_dealer))) {
            return new QuoteFormDealerFragment();
        }

        if(key.equals(context.getString(R.string.tab_key_quote))) {
            return new QuoteFormFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        if(pages.length == keys.length) {
            return keys.length;
        } throw new IllegalStateException("Key length must match page title length");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages[position];
    }
}
