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

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Quote {

    @SerializedName("vehicle")
    public Vehicle vehicle;

    @SerializedName("dealer")
    public Dealer dealer;

    @SerializedName("price")
    public String price;

    @SerializedName("residual")
    public String residual;

    @SerializedName("taxRate")
    public String taxRate;

    @SerializedName("moneyFactor")
    public String moneyFactor;

    @SerializedName("interestRate")
    public String interestRate;

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

    @SerializedName("fees")
    public Map<String, Fee> fees;

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

    public Quote() {}

    public Quote(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        result.put("vehicle", (vehicle != null) ? vehicle.toMap() : null);
        result.put("dealer", (dealer != null) ? dealer.toMap() : null);
        result.put("price", price);
        result.put("residual", residual);
        result.put("taxRate", taxRate);
        result.put("moneyFactor", moneyFactor);
        result.put("interestRate", interestRate);
        result.put("term", term);
        result.put("rebate", rebate);
        result.put("downPayment", downPayment);
        result.put("tradeValue", tradeValue);
        result.put("tradeOwed", tradeOwed);
        result.put("fees", fees);
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
                ", dealer=" + dealer +
                ", price='" + price + '\'' +
                ", residual='" + residual + '\'' +
                ", taxRate='" + taxRate + '\'' +
                ", moneyFactor='" + moneyFactor + '\'' +
                ", interestRate='" + interestRate + '\'' +
                ", term='" + term + '\'' +
                ", rebate='" + rebate + '\'' +
                ", downPayment='" + downPayment + '\'' +
                ", tradeValue='" + tradeValue + '\'' +
                ", tradeOwed='" + tradeOwed + '\'' +
                ", fees=" + fees +
                ", totalCost='" + totalCost + '\'' +
                ", monthlyPayment='" + monthlyPayment + '\'' +
                ", dueAtSigning='" + dueAtSigning + '\'' +
                ", created='" + created + '\'' +
                ", edited=" + edited +
                '}';
    }
}
