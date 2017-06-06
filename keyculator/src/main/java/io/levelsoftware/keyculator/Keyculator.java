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

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import timber.log.Timber;


public class Keyculator extends FrameLayout implements View.OnClickListener {

    private View view;
    private View scrollView;
    
    private int keyboardHeight;
    private int initialScrollViewHeight;
    private float offScreenPosition;

    public ValueAnimator keyboardEnterAnimator;
    public ValueAnimator keyboardExitAnimator;
    public ValueAnimator scrollViewCollapseAnimator;
    public ValueAnimator scrollViewExpandAnimator;

    private AnimatorSet enterAnimatorSet;
    private AnimatorSet exitAnimatorSet;

    private int exitAnimationDuration;
    private int enterAnimationDuration;

    private SparseArray<TextView> buttons = new SparseArray<>();

    private TextView screen;
    private TextView result;

    private OnEventListener listener;

    protected static final int EVENT_KEYBOARD_OPENED = 0;
    protected static final int EVENT_KEYBOARD_CLOSED = 1;
    protected static final int EVENT_KEYBOARD_RESULT = 2;

    public Keyculator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_keyculator, this);
        
        enterAnimationDuration = getResources().getInteger(R.integer.default_enter_duration);
        exitAnimationDuration = getResources().getInteger(R.integer.default_exit_duration);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        configureDimensions();
        setupAnimators();

        screen = (TextView) findViewById(R.id.text_view_screen);
        result = (TextView) findViewById(R.id.text_view_result);

        collectButtons((ViewGroup)findViewById(R.id.container));
        attachListeners();
    }
    
    private void configureDimensions() {
        this.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams params = (LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.BOTTOM;

                if(params.height <= 0) {
                    keyboardHeight = getResources().getDimensionPixelSize(R.dimen.default_keyboard_height);
                    params.height = keyboardHeight;
                }
                view.setLayoutParams(params);

                Timber.d("Current keyboard position: " + view.getY() +
                        " Offscreen position: " + offScreenPosition +
                        " Keyboard height: " + view.getMeasuredHeight());

                // Move the keyboard off the screen initially
                view.setY(view.getY() + keyboardHeight);

                // Update the value of the off screen position once the layout has been completely setup
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        offScreenPosition = view.getY();

                        Timber.d("Current keyboard position: " + view.getY() +
                                " Offscreen position: " + offScreenPosition +
                                " Keyboard height: " + view.getMeasuredHeight());
                    }
                });
            }
        });
    }

    protected void collectButtons(ViewGroup parent) {
        for(int i = 0; i<parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child instanceof TextView) {
                Timber.d("Found button: " + ((TextView) child).getText());
                buttons.put(child.getId(), (TextView) child);
            } else if(child instanceof ViewGroup) {
                collectButtons((ViewGroup) child);
            }
        }
    }

    private void attachListeners() {
        for(int i = 0; i<buttons.size(); i++) {
            TextView button = buttons.get(buttons.keyAt(i));
            button.setOnClickListener(this);
        }

        findViewById(R.id.backspace).setOnClickListener(this);
    }

    private void setupAnimators() {
        ValueAnimator.AnimatorUpdateListener keyboardUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setY((float)animation.getAnimatedValue());
            }
        };

        ValueAnimator.AnimatorUpdateListener scrollViewUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                layoutParams.height = (Integer)animation.getAnimatedValue();
                scrollView.setLayoutParams(layoutParams);
            }
        };

        keyboardEnterAnimator = ValueAnimator.ofFloat();
        keyboardEnterAnimator.addUpdateListener(keyboardUpdateListener);
        keyboardEnterAnimator.setDuration(enterAnimationDuration);
        keyboardEnterAnimator.setInterpolator(new FastOutSlowInInterpolator());

        scrollViewCollapseAnimator = ValueAnimator.ofInt();
        scrollViewCollapseAnimator.addUpdateListener(scrollViewUpdateListener);
        scrollViewCollapseAnimator.setDuration(enterAnimationDuration);
        scrollViewCollapseAnimator.setInterpolator(keyboardEnterAnimator.getInterpolator());

        enterAnimatorSet = new AnimatorSet();
        enterAnimatorSet.playTogether(keyboardEnterAnimator, scrollViewCollapseAnimator);

        keyboardExitAnimator = ValueAnimator.ofFloat();
        keyboardExitAnimator.addUpdateListener(keyboardUpdateListener);
        keyboardExitAnimator.setDuration(exitAnimationDuration);
        keyboardExitAnimator.setInterpolator(new AccelerateInterpolator());

        scrollViewExpandAnimator = ValueAnimator.ofInt();
        scrollViewExpandAnimator.addUpdateListener(scrollViewUpdateListener);
        scrollViewExpandAnimator.setDuration(exitAnimationDuration);
        scrollViewExpandAnimator.setInterpolator(keyboardExitAnimator.getInterpolator());

        exitAnimatorSet = new AnimatorSet();
        exitAnimatorSet.playTogether(keyboardExitAnimator, scrollViewExpandAnimator);
    }

    /**
     * Show the keyboard and contract the scrolling view.
     */
    public void showKeyboard() {
        // If the keyboard is already open don't do anything
        if(keyboardIsActive() || keyboardIsEntering()) {
            Timber.v("Keyboard is already fully visible or animating in");
            return;
        }

        if(!keyboardIsEntering()) {
            if(keyboardIsHiding()) {
                exitAnimatorSet.cancel();
            }
            Timber.d("Current keyboard position: " + view.getY() +
                    " Offscreen position: " + offScreenPosition +
                    " Keyboard height: " + keyboardHeight);

            keyboardEnterAnimator.setFloatValues(view.getY(), offScreenPosition - keyboardHeight);
            scrollViewCollapseAnimator.setIntValues(scrollView.getMeasuredHeight(),
                    initialScrollViewHeight - keyboardHeight
                            + getResources().getDimensionPixelSize(R.dimen.keyboard_top_margin));

            enterAnimatorSet.start();

            sendEvent(EVENT_KEYBOARD_OPENED);
        }
    }

    /**
     * Hide the keyboard and expand the scrolling view.
     */
    public void hideKeyboard() {
        // If the keyboard is already off screen don't do anything
        if(keyboardIsHidden() || keyboardIsHiding()) {
            Timber.v("Keyboard is already fully hidden or hiding");
            return;
        }

        if(!keyboardIsHiding()) {
            if(keyboardIsEntering()) {
                enterAnimatorSet.cancel();
            }

            Timber.d("Current keyboard position: " + view.getY() +
                    " Offscreen position: " + offScreenPosition +
                    " Keyboard height: " + keyboardHeight);

            keyboardExitAnimator.setFloatValues(view.getY(), offScreenPosition);
            scrollViewExpandAnimator.setIntValues(scrollView.getMeasuredHeight(), initialScrollViewHeight);

            exitAnimatorSet.start();

            sendEvent(EVENT_KEYBOARD_CLOSED);
        }
    }

    private boolean keyboardIsAnimating(@NonNull ValueAnimator animator) {
        return animator.isStarted() || animator.isRunning();
    }

    /**
     * Checks if the keyboard is in the process of entering.
     *
     * @return true if the keyboard is currently animating in
     */
    public boolean keyboardIsEntering() {
        return keyboardIsAnimating(keyboardEnterAnimator);
    }

    /**
     * Checks if the keyboard is in the process of hiding.
     * 
     * @return true if the keyboard is currently animating out
     */
    public boolean keyboardIsHiding() {
        return keyboardIsAnimating(keyboardExitAnimator);
    }

    /**
     * Sets the scrolling view whose height will be adjusted when opening or closing the keyboard.
     *
     * @param view scrolling view (should be ScrollView or RecyclerView)
     */
    public void setScrollView(@NonNull View view) {
        this.scrollView = view;

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                initialScrollViewHeight = scrollView.getMeasuredHeight();
                Timber.d("Initial scroll view height: " + initialScrollViewHeight);
            }
        });
    }

    /**
     * Sets the class to be used for event callbacks. There are two ways to receive events
     * from this keyboard. By directly associating an OnEventListener class through this
     * method or by subscribing to local broadcasts with a KeyculatorBroadcastReceiver.
     *
     * @param listener class that implements OnEventListener interface
     */
    public void setEventListener(OnEventListener listener) {
        this.listener = listener;
    }

    /**
     * Determine if the keyboard is fully visible. Can be used with keyboardIsEntering to determine
     * the state of the keyboard.
     *
     * @return true if the keyboard is fully up
     */
    public boolean keyboardIsActive() {
        return view.getY() == offScreenPosition - keyboardHeight;
    }

    /**
     * Determine if the keyboard is completed hidden. Can be used with keyboardIsHiding to determine
     * the state of the keyboard.
     *
     * @return true if the keyboard is fully off screen
     */
    public boolean keyboardIsHidden() {
        return view.getY() == offScreenPosition;
    }

    @Override
    public void onClick(View view) {
        if(view instanceof TextView) {
            Timber.v("Got button click event: " + ((TextView) view).getText());
        }

        if(view.getId() == R.id.backspace) {
            Timber.v("Pressed backspace");
        }
    }

    private void sendEvent(int code, double result) {
        if(listener != null) {
            switch (code) {
                case EVENT_KEYBOARD_OPENED:
                    listener.keyboardOpened();
                    break;

                case EVENT_KEYBOARD_CLOSED:
                    listener.keyboardClosed();
                    break;

                case EVENT_KEYBOARD_RESULT:
                    listener.keyboardResult(result);
            }
        }

        Intent event = new Intent(KeyculatorBroadcastReceiver.ACTION);
        event.putExtra(KeyculatorBroadcastReceiver.INTENT_KEY_EVENT_CODE, code);
        event.putExtra(KeyculatorBroadcastReceiver.INTENT_KEY_RESULT, result);
        LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(event);
    }

    private void sendEvent(int code) {
        sendEvent(code, 0);
    }

    public interface OnEventListener {
        void keyboardOpened();
        void keyboardClosed();
        void keyboardResult(double result);
    }
}
