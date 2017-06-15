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

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Model implements Parcelable {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("niceName")
    public String niceName;

    @SerializedName("currentYear")
    public Integer currentYear;

    @SerializedName("basePrice")
    public String basePrice;

    @SerializedName("photoPath")
    public String photoPath;

    public Model() {}

    public Model(String id, String name, String niceName, Integer currentYear, String basePrice, String photoPath) {
        this.id = id;
        this.name = name;
        this.niceName = niceName;
        this.currentYear = currentYear;
        this.basePrice = basePrice;
        this.photoPath = photoPath;
    }

    protected Model(Parcel in) {
        id = in.readString();
        name = in.readString();
        niceName = in.readString();
        currentYear = in.readInt();
        basePrice = in.readString();
        photoPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(niceName);
        dest.writeInt((currentYear == null) ? 0 : currentYear);
        dest.writeString(basePrice);
        dest.writeString(photoPath);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("name", name);
        result.put("niceName", niceName);
        result.put("currentYear", currentYear);
        result.put("basePrice", basePrice);
        result.put("photoPath", photoPath);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", niceName='" + niceName + '\'' +
                ", currentYear=" + currentYear +
                ", basePrice=" + basePrice +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model model = (Model) o;

        if (id != null ? !id.equals(model.id) : model.id != null) return false;
        if (name != null ? !name.equals(model.name) : model.name != null) return false;
        if (niceName != null ? !niceName.equals(model.niceName) : model.niceName != null) return false;
        if (currentYear != null ? !currentYear.equals(model.currentYear) : model.currentYear != null) return false;
        //noinspection SimplifiableIfStatement
        if (basePrice != null ? !basePrice.equals(model.basePrice) : model.basePrice != null) return false;
        return photoPath != null ? photoPath.equals(model.photoPath) : model.photoPath == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (niceName != null ? niceName.hashCode() : 0);
        result = 31 * result + (currentYear != null ? currentYear.hashCode() : 0);
        result = 31 * result + (basePrice != null ? basePrice.hashCode() : 0);
        result = 31 * result + (photoPath != null ? photoPath.hashCode() : 0);
        return result;
    }
}
