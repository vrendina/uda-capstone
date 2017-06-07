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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class StringNumber implements Parcelable {

    private String characteristic;
    private String mantissa;
    private boolean decimal;

    /**
     * Create a new StringNumber object with the supplied value.
     *
     * @param number value of the number
     */
    public StringNumber(@Nullable String number) {
        if(TextUtils.isEmpty(number)) {
            return;
        }
        decomposeNumber(number);
    }

    public StringNumber(@Nullable BigDecimal number) {
        if(number == null) {
            return;
        }
        decomposeNumber(number.toPlainString());
    }

    protected StringNumber(Parcel in) {
        characteristic = in.readString();
        mantissa = in.readString();
        decimal = in.readByte() != 0;
    }

    /**
     * Retrieve the characteristic of the number.
     *
     * @return characteristic or null if it doesn't exist
     */
    @Nullable
    public String getCharacteristic() {
        return characteristic;
    }

    /**
     * Retrieve the mantissa of the number.
     *
     * @return mantissa or null if it doesn't exist
     */
    @Nullable
    public String getMantissa() {
        return mantissa;
    }

    public boolean hasDecimalPoint() {
        return decimal;
    }

    @Nullable
    public String getStringValue() {
        if(mantissa != null) {
            return characteristic + "." + mantissa;
        }

        return (decimal) ? characteristic + "." : characteristic;
    }

    @Nullable
    public BigDecimal getDecimalValue() {
        return (getStringValue() != null) ? new BigDecimal(getStringValue()) : null;
    }

    @Nullable
    public BigInteger getIntegerCharacteristic() {
        return (getCharacteristic() != null) ? new BigInteger(getCharacteristic()) : null;
    }

    private void decomposeNumber(@NonNull String number) {
        String[] components = number.split("\\.");

        if(components.length > 0) {
            if(components[0].length() > 0) {
                characteristic = components[0];
            }
        }

        if(components.length > 1) {
            if(components[1].length() > 0) {
                mantissa = components[1];
            }
        }

        if(number.contains(".")) {
            decimal = true;
        }
    }

    @Override
    public String toString() {
        return "StringNumber{" +
                "characteristic='" + characteristic + '\'' +
                ", mantissa='" + mantissa + '\'' +
                ", decimal=" + decimal +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(characteristic);
        dest.writeString(mantissa);
        dest.writeByte((byte) (decimal ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StringNumber> CREATOR = new Creator<StringNumber>() {
        @Override
        public StringNumber createFromParcel(Parcel in) {
            return new StringNumber(in);
        }

        @Override
        public StringNumber[] newArray(int size) {
            return new StringNumber[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringNumber that = (StringNumber) o;

        return decimal == that.decimal &&
                (getCharacteristic() != null ? getCharacteristic().equals(that.getCharacteristic()) : that.getCharacteristic() == null &&
                (getMantissa() != null ? getMantissa().equals(that.getMantissa()) : that.getMantissa() == null));

    }

    @Override
    public int hashCode() {
        int result = getCharacteristic() != null ? getCharacteristic().hashCode() : 0;
        result = 31 * result + (getMantissa() != null ? getMantissa().hashCode() : 0);
        result = 31 * result + (decimal ? 1 : 0);
        return result;
    }
}
