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

package io.levelsoftware.carculator.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Make implements Parcelable {
    @SerializedName("name")
    public String name;

    @SerializedName("niceName")
    public String niceName;

    @SerializedName("models")
    public List<Model> models = null;

    public Make() {}

    public Make(String name, String niceName, List<Model> models) {
        this.name = name;
        this.niceName = niceName;
        this.models = models;
    }

    protected Make(Parcel in) {
        name = in.readString();
        niceName = in.readString();
        models = in.createTypedArrayList(Model.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(niceName);
        dest.writeTypedList(models);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Make> CREATOR = new Creator<Make>() {
        @Override
        public Make createFromParcel(Parcel in) {
            return new Make(in);
        }

        @Override
        public Make[] newArray(int size) {
            return new Make[size];
        }
    };

    @Override
    public String toString() {
        return "Make{" +
                "name='" + name + '\'' +
                ", niceName='" + niceName + '\'' +
                ", models=" + models +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Make make = (Make) o;

        if (name != null ? !name.equals(make.name) : make.name != null) return false;
        //noinspection SimplifiableIfStatement
        if (niceName != null ? !niceName.equals(make.niceName) : make.niceName != null) return false;
        return models != null ? models.equals(make.models) : make.models == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (niceName != null ? niceName.hashCode() : 0);
        result = 31 * result + (models != null ? models.hashCode() : 0);
        return result;
    }
}
