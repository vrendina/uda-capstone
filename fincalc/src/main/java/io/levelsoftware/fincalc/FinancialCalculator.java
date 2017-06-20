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

package io.levelsoftware.fincalc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.levelsoftware.fincalc.list.Fee;
import io.levelsoftware.fincalc.list.Rebate;

public abstract class FinancialCalculator {

    protected static final BigDecimal DECIMAL_ONE_HUNDRED = new BigDecimal("100");
    protected static final int DEFAULT_DIVISION_SCALE = 32;

    private BigDecimal price;
    private BigDecimal downPayment;
    private Integer term;
    private BigDecimal taxPercentage;
    private BigDecimal tradeValue;
    private BigDecimal tradeOwed;
    private boolean taxCapitalized;

    private List<Fee> fees;
    private List<Rebate> rebates;

    public abstract BigDecimal getTotalCost();
    public abstract BigDecimal getMonthlyPayment();
    public abstract BigDecimal getDueAtSigning();

    public abstract BigDecimal getTotalTax();
    public abstract BigDecimal getNetCapitalizedCost();

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = (price == null) ? BigDecimal.ZERO : price;
    }

    public void setPrice(String price) {
        this.price = (price == null) ? BigDecimal.ZERO : new BigDecimal(price);
    }

    public BigDecimal getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(BigDecimal downPayment) {
        this.downPayment = (downPayment == null) ? BigDecimal.ZERO : downPayment;
    }

    public void setDownPayment(String downPayment) {
        this.downPayment = (downPayment == null) ? BigDecimal.ZERO : new BigDecimal(downPayment);
    }

    public Integer getTerm() {
        return term;
    }

    /**
     * Sets the duration of the loan in months. If the term is set to zero, then no interest
     * will be paid and the entire balance is due up front.
     *
     * @param term duration of loan in months
     */
    public void setTerm(Integer term) {
        this.term = (term == null) ? 0 : term;
    }

    public void setTerm(String term) {
        this.term = (term == null) ? 0 : Integer.parseInt(term);
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = (taxPercentage == null) ? BigDecimal.ZERO : taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = (taxPercentage == null) ? BigDecimal.ZERO : new BigDecimal(taxPercentage);
    }

    public BigDecimal getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(BigDecimal tradeValue) {
        this.tradeValue = (tradeValue == null) ? BigDecimal.ZERO : tradeValue;
    }

    public void setTradeValue(String tradeValue) {
        this.tradeValue = (tradeValue == null) ? BigDecimal.ZERO : new BigDecimal(tradeValue);
    }

    public BigDecimal getTradeOwed() {
        return tradeOwed;
    }

    public void setTradeOwed(BigDecimal tradeOwed) {
        this.tradeOwed = (tradeOwed == null) ? BigDecimal.ZERO : tradeOwed;
    }

    public void setTradeOwed(String tradeOwed) {
        this.tradeOwed = (tradeOwed == null) ? BigDecimal.ZERO : new BigDecimal(tradeOwed);
    }

    public boolean isTaxCapitalized() {
        return taxCapitalized;
    }

    public void setTaxCapitalized(boolean taxCapitalized) {
        this.taxCapitalized = taxCapitalized;
    }

    public List<Fee> getFees() {
        return fees;
    }

    public void setFees(List<Fee> fees) {
        this.fees = (fees == null) ? new ArrayList<Fee>() : fees;
    }

    public List<Rebate> getRebates() {
        return rebates;
    }

    public void setRebates(List<Rebate> rebates) {
        this.rebates = (rebates == null) ? new ArrayList<Rebate>() : rebates;
    }

    protected BigDecimal getTradeCredit() {
        return getTradeValue().subtract(getTradeOwed());
    }

    protected BigDecimal getTotalRebates() {
        BigDecimal total = BigDecimal.ZERO;
        for(Rebate rebate: getRebates()) {
            total = total.add(rebate.getAmount());
        }
        return total;
    }

    protected BigDecimal getNonTaxableRebates() {
        BigDecimal total = BigDecimal.ZERO;
        for(Rebate rebate: getRebates()) {
            if(!rebate.isTaxable()) {
                total = total.add(rebate.getAmount());
            }
        }
        return total;
    }

    protected BigDecimal getTaxableFees() {
        BigDecimal total = BigDecimal.ZERO;
        for(Fee fee: getFees()) {
            if(fee.isTaxable()) {
                total = total.add(fee.getAmount());
            }
        }
        return total;
    }

    protected BigDecimal getCapitalizedFees() {
        BigDecimal total = BigDecimal.ZERO;
        for(Fee fee: getFees()) {
            if(fee.isCapitalized()) {
                total = total.add(fee.getAmount());
            }
        }
        return total;
    }

    protected BigDecimal getNonCapitalizedFees() {
        BigDecimal total = BigDecimal.ZERO;
        for(Fee fee: getFees()) {
            if(!fee.isCapitalized()) {
                total = total.add(fee.getAmount());
            }
        }
        return total;
    }

    protected BigDecimal getTaxRate() {
        return getTaxPercentage().divide(DECIMAL_ONE_HUNDRED, DEFAULT_DIVISION_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }

}

