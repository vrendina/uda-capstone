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

package io.levelsoftware.carculator.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

public class UserUtils {

    private static UserUtils utils;

    private FirebaseAuth auth;
    private FirebaseUser user;

    // This variable is needed to prevent duplicate anonymous login
    private boolean isLoggingIn;

    private UserUtils() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private static void initialize() {
        utils = new UserUtils();
    }

    @Nullable
    public static String getUid() {
        if(utils == null) {
            initialize();
        }

        utils.signInUser();
        return (utils.user != null) ? utils.user.getUid() : null;
    }


    private void signInUser() {
        if(!isLoggingIn && user == null) {
            isLoggingIn = true;
            auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        user = auth.getCurrentUser();

                        if(user != null) {
                            Timber.d("New user created -- " + user.getUid());
                        }
                    } else {
                        Timber.e(task.getException(), "Problem with user sign in");
                    }

                    isLoggingIn = false;
                }
            });
        }
    }

}
