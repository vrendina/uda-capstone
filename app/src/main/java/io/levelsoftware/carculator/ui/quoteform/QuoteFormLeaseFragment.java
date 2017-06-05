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

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.carculator.R;
import io.levelsoftware.carculator.ui.FormField;
import timber.log.Timber;


public class QuoteFormLeaseFragment extends QuoteFormFragment
        implements FormField.OnFormFieldEventListener {

    @BindView(R.id.button_test) Button testButton;
    @BindView(R.id.scroll_view) ScrollView scrollView;
    @BindView(R.id.linear_layout_form_container) LinearLayout formContainerLinearLayout;

    private SparseArray<FormField> fields = new SparseArray<>();

    private int defaultScrollViewHeight;
    private ValueAnimator scrollViewAnimator;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quote_lease_form, container, false);
        ButterKnife.bind(this, view);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testClicked();
            }
        });

        collectFields(formContainerLinearLayout);
        attachListeners();

        configureScrollViewAnimator();

        // Get the height of the ScrollView once layout is complete
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                defaultScrollViewHeight = scrollView.getMeasuredHeight();
            }
        });

        return view;
    }

    private void collectFields(ViewGroup parent) {
        for(int i = 0; i<parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if(child instanceof FormField) {
                Timber.d("Found form field " + child.toString());
                fields.put(child.getId(), (FormField) child);
            } else if(child instanceof ViewGroup) {
                collectFields((ViewGroup) child);
            }
        }
    }

    private void attachListeners() {
        for(int i = 0; i<fields.size(); i++) {
            FormField field = fields.get(fields.keyAt(i));
            field.setOnFormFieldEventListener(this);
        }
    }


    private void configureScrollViewAnimator() {
        scrollViewAnimator = ValueAnimator.ofInt();
        scrollViewAnimator.setDuration(4000);

        scrollViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                layoutParams.height = val;
                scrollView.setLayoutParams(layoutParams);
            }
        });
    }

    public void testClicked() {
        hideKeyboard();
    }

    @Override
    public void fieldFocusChanged(@IdRes int id, boolean hasFocus) {
        Timber.d("Focus changed for " + id + " has focus: " + hasFocus);

        if(hasFocus) {
            showKeyboard();

            FormField field = fields.get(id);
            Rect rect = new Rect(0, 0, field.getWidth(), field.getHeight());
            field.requestRectangleOnScreen(rect, false);
        }
    }

    private void showKeyboard() {
        resizeScrollView(true);
    }

    private void hideKeyboard() {
        resizeScrollView(false);
        formContainerLinearLayout.requestFocus();
    }

    private void resizeScrollView(boolean showKeyboard) {

        int keyboardHeight = 400;

        if(showKeyboard) {
            if(scrollView.getMeasuredHeight() == defaultScrollViewHeight - keyboardHeight) {
                return;
            }

            // If we are already animating the keyboard in, don't start the animation again
            if(scrollViewAnimator.isStarted()
                    && scrollViewAnimator.getInterpolator() instanceof FastOutSlowInInterpolator) {
                return;
            }
            scrollViewAnimator.cancel();

            scrollViewAnimator.setInterpolator(new FastOutSlowInInterpolator());
            scrollViewAnimator.setIntValues(scrollView.getMeasuredHeight(), defaultScrollViewHeight - keyboardHeight);

        } else {
            if(scrollView.getMeasuredHeight() == defaultScrollViewHeight) {
                return;
            }
            // If we are already animating the keyboard out, don't start the animation again
            if(scrollViewAnimator.isStarted()
                    && scrollViewAnimator.getInterpolator() instanceof AccelerateInterpolator) {
                return;
            }

            scrollViewAnimator.setInterpolator(new AccelerateInterpolator());
            scrollViewAnimator.setIntValues(scrollView.getMeasuredHeight(), defaultScrollViewHeight);
        }

        scrollViewAnimator.start();
    }
}
