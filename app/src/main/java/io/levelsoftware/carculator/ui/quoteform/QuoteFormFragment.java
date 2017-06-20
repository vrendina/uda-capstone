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

import io.levelsoftware.carculator.model.quote.Quote;


public abstract class QuoteFormFragment extends Fragment {

    protected QuoteManager quoteManager;

    public QuoteFormFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            quoteManager = (QuoteManager) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement QuoteManager interface");
        }
    }

    // Compare if two strings are equivalent -- they could both be null and be equivalent
    protected boolean same(String one, String two) {
        return one == null && two == null || one != null && one.equals(two);
    }

    protected interface QuoteManager {
        Quote getQuote();
        void notifyDataChanged();
    }

}
