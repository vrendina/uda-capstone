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

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Quote implements Parcelable {

    @SerializedName("vehicle")
    public Vehicle vehicle;

//    @SerializedName("dealer")
//    public Dealer dealer;

    @SerializedName("price")
    public String price;

    @SerializedName("residual")
    public String residual;

    @SerializedName("taxPercentage")
    public String taxPercentage;

    @SerializedName("moneyFactor")
    public String moneyFactor;

    @SerializedName("interestPercentage")
    public String interestPercentage;

    @SerializedName("term")
    public String term;

    @SerializedName("rebate")
    public String rebate;

    @SerializedName("downPayment")
    public String downPayment;

    @SerializedName("tradeValue")
    public String tradeValue;

    @SerializedName("tradeOwed")
    public String tradeOwed;

//    @SerializedName("fees")
//    public Map<String, Fee> fees;

    @SerializedName("totalCost")
    public String totalCost;

    @SerializedName("monthlyPayment")
    public String monthlyPayment;

    @SerializedName("dueAtSigning")
    public String dueAtSigning;

    @SerializedName("created")
    public String created;

    @Exclude
    public boolean edited;

    @Exclude
    public String id;

    @Exclude
    public String type;

    public Quote() {}

    public Quote(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    protected Quote(Parcel in) {
        vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        price = in.readString();
        residual = in.readString();
        taxPercentage = in.readString();
        moneyFactor = in.readString();
        interestPercentage = in.readString();
        term = in.readString();
        rebate = in.readString();
        downPayment = in.readString();
        tradeValue = in.readString();
        tradeOwed = in.readString();
        totalCost = in.readString();
        monthlyPayment = in.readString();
        dueAtSigning = in.readString();
        created = in.readString();
        edited = in.readByte() != 0;
        id = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(vehicle, flags);
        dest.writeString(price);
        dest.writeString(residual);
        dest.writeString(taxPercentage);
        dest.writeString(moneyFactor);
        dest.writeString(interestPercentage);
        dest.writeString(term);
        dest.writeString(rebate);
        dest.writeString(downPayment);
        dest.writeString(tradeValue);
        dest.writeString(tradeOwed);
        dest.writeString(totalCost);
        dest.writeString(monthlyPayment);
        dest.writeString(dueAtSigning);
        dest.writeString(created);
        dest.writeByte((byte) (edited ? 1 : 0));
        dest.writeString(id);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("vehicle", (vehicle != null) ? vehicle.toMap() : null);
//        result.put("dealer", (dealer != null) ? dealer.toMap() : null);
        result.put("price", price);
        result.put("residual", residual);
        result.put("taxPercentage", taxPercentage);
        result.put("moneyFactor", moneyFactor);
        result.put("interestPercentage", interestPercentage);
        result.put("term", term);
        result.put("rebate", rebate);
        result.put("downPayment", downPayment);
        result.put("tradeValue", tradeValue);
        result.put("tradeOwed", tradeOwed);
//        result.put("fees", fees);
        result.put("totalCost", totalCost);
        result.put("monthlyPayment", monthlyPayment);
        result.put("dueAtSigning", dueAtSigning);
        result.put("created", created);

        return result;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "vehicle=" + vehicle +
                ", price='" + price + '\'' +
                ", residual='" + residual + '\'' +
                ", taxPercentage='" + taxPercentage + '\'' +
                ", moneyFactor='" + moneyFactor + '\'' +
                ", interestPercentage='" + interestPercentage + '\'' +
                ", term='" + term + '\'' +
                ", rebate='" + rebate + '\'' +
                ", downPayment='" + downPayment + '\'' +
                ", tradeValue='" + tradeValue + '\'' +
                ", tradeOwed='" + tradeOwed + '\'' +
                ", totalCost='" + totalCost + '\'' +
                ", monthlyPayment='" + monthlyPayment + '\'' +
                ", dueAtSigning='" + dueAtSigning + '\'' +
                ", created='" + created + '\'' +
                ", edited=" + edited +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
