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

package io.levelsoftware.keyculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class KeyculatorBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "io.levelsoftware.keyculator.KEYBOARD_EVENT";

    protected static final String INTENT_KEY_EVENT_CODE = "event_code";
    protected static final String INTENT_KEY_CHARACTERISTIC = "characteristic";
    protected static final String INTENT_KEY_MANTISSA = "mantissa";

    private Keyculator.OnEventListener listener;

    public KeyculatorBroadcastReceiver(Keyculator.OnEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int code = intent.getIntExtra(INTENT_KEY_EVENT_CODE, -1);
//        double result = intent.getDoubleExtra(INTENT_KEY_RESULT, 0);


        switch (code) {
            case Keyculator.EVENT_KEYBOARD_OPENED:
                listener.keyboardOpened();
                break;

            case Keyculator.EVENT_KEYBOARD_CLOSED:
                listener.keyboardClosed();
                break;

            case Keyculator.EVENT_KEYBOARD_RESULT:
//                listener.keyboardResult(result);
        }
    }

}