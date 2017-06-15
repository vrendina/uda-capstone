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

package io.levelsoftware.carculator.model.quote;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import io.levelsoftware.carculator.model.Make;
import io.levelsoftware.carculator.model.Model;

@IgnoreExtraProperties
public class Vehicle implements Parcelable {

    @SerializedName("model")
    public Model model;

    @SerializedName("make")
    public Make make;

    public Vehicle() {}

    public Vehicle(Make make, Model model) {
        this.make = make;
        this.model = model;
    }

    protected Vehicle(Parcel in) {
        model = in.readParcelable(Model.class.getClassLoader());
        make = in.readParcelable(Make.class.getClassLoader());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("model", model.toMap());
        result.put("make", make.toMap());

        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(model, flags);
        dest.writeParcelable(make, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    @Override
    public String toString() {
        return "Vehicle{" +
                "model=" + model +
                ", make=" + make +
                '}';
    }
}
