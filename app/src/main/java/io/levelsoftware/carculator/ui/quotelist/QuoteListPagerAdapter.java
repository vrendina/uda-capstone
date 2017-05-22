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

package io.levelsoftware.carculator.ui.quotelist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class QuoteListPagerAdapter extends FragmentPagerAdapter {

    private String[] pageTitles;
    private String[] pageKeys;

    public QuoteListPagerAdapter(FragmentManager fragmentManager,
                                 String[] pageTitles, String[] pageKeys) {
        super(fragmentManager);

        this.pageTitles = pageTitles;
        this.pageKeys = pageKeys;
    }

    @Override
    public Fragment getItem(int position) {
        return QuoteListFragment.newInstance(pageKeys[position]);
    }

    @Override
    public int getCount() {
        return pageTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}