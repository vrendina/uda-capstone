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

public class LoanCalculator extends FinancialCalculator {

    private BigDecimal interestPercentage;

    private LoanCalculator(Builder builder) {
        setPrice(builder.price);
        setInterestPercentage(builder.interestPercentage);
        setTerm(builder.term);

        setDownPayment(builder.downPayment);
        setTaxPercentage(builder.taxPercentage);
        setTaxCapitalized(builder.taxCapitalized);
        setTradeValue(builder.tradeValue);
        setTradeOwed(builder.tradeOwed);
        setFees(builder.fees);
        setRebates(builder.rebates);
    }

    public BigDecimal getInterestPercentage() {
        return interestPercentage;
    }

    public void setInterestPercentage(BigDecimal interestPercentage) {
        this.interestPercentage = (interestPercentage == null) ? BigDecimal.ZERO : interestPercentage;
    }

    @Override
    public BigDecimal getTotalCost() {
        BigDecimal total = getTotalLoanCost().add(getDownPayment()).add(getNonCapitalizedFees());

        if(!isTaxCapitalized()) {
            total = total.add(getTotalTax());
        }

        return total;
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        return getAmortizationCoefficient().multiply(getNetCapitalizedCost());
    }

    @Override
    public BigDecimal getDueAtSigning() {
        BigDecimal total = getDownPayment().add(getNonCapitalizedFees());

        if(!isTaxCapitalized()) {
            total = total.add(getTotalTax());
        }

        if(getTerm() == 0) {
            total = total.add(getTotalLoanCost());
        }

        return total;
    }

    @Override
    public BigDecimal getTotalTax() {
        return getVehicleTax().add(getFeeTax());
    }

    @Override
    public BigDecimal getNetCapitalizedCost() {
        return getCapitalizedVehicle().add(getCapitalizedTax()).add(getCapitalizedFees());
    }

    public BigDecimal getTotalLoanCost() {
        if(getTerm() == 0) {
            return getNetCapitalizedCost();
        }
        return getMonthlyPayment().multiply(new BigDecimal(getTerm()));
    }

    public BigDecimal getTotalInterest() {
        return getTotalLoanCost().subtract(getNetCapitalizedCost());
    }

    protected BigDecimal getVehicleTaxBasis() {
        return getPrice().subtract(getNonTaxableRebates());
    }

    protected BigDecimal getVehicleTax() {
        return getVehicleTaxBasis().multiply(getTaxRate());
    }

    protected BigDecimal getFeeTax() {
        return getTaxableFees().multiply(getTaxRate());
    }

    protected BigDecimal getCapitalizedVehicle() {
        return getPrice().subtract(getDownPayment()).subtract(getTotalRebates()).subtract(getTradeCredit());
    }

    protected BigDecimal getCapitalizedTax() {
        return (isTaxCapitalized()) ? getTotalTax() : BigDecimal.ZERO;
    }

    protected BigDecimal getPeriodicInterestRate() {
        return getInterestRate()
                .divide(new BigDecimal("12"), DEFAULT_DIVISION_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }

    protected BigDecimal getAmortizationCoefficient() {
        // If there are zero periods in the loan, the coefficient is 0
        if(getTerm() == 0) {
            return BigDecimal.ZERO;
        }

        // If we have a zero % interest loan, the amortization coefficient is 1/period
        if(getInterestRate().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE.divide(new BigDecimal(getTerm()),
                    DEFAULT_DIVISION_SCALE, BigDecimal.ROUND_HALF_EVEN);
        }

        BigDecimal amortizationFactor = BigDecimal.ONE.add(getPeriodicInterestRate()).pow(getTerm());

        return getPeriodicInterestRate().multiply(amortizationFactor)
                .divide(amortizationFactor.subtract(BigDecimal.ONE),
                        DEFAULT_DIVISION_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }

    protected BigDecimal getInterestRate() {
        return getInterestPercentage()
                .divide(DECIMAL_ONE_HUNDRED, DEFAULT_DIVISION_SCALE, BigDecimal.ROUND_HALF_EVEN);
    }

    public static class Builder {

        private BigDecimal price;
        private BigDecimal downPayment;
        private Integer term;
        private BigDecimal taxPercentage;
        private BigDecimal tradeValue;
        private BigDecimal tradeOwed;

        private BigDecimal interestPercentage;

        private List<Fee> fees = new ArrayList<>();
        private List<Rebate> rebates = new ArrayList<>();
        
        private boolean taxCapitalized;
        
        public Builder(BigDecimal price, BigDecimal interestPercentage, Integer term) {
            this.price = price;
            this.interestPercentage = interestPercentage;
            this.term = term;
        }
        
        public Builder tax(BigDecimal percentage, boolean capitalized) {
            this.taxPercentage = percentage;
            this.taxCapitalized = capitalized;
            return this;
        }
        
        public Builder downPayment(BigDecimal downPayment) {
            this.downPayment = downPayment;
            return this;
        }
        
        public Builder tradeValue(BigDecimal tradeValue) {
            this.tradeValue = tradeValue;
            return this;
        }

        public Builder tradeOwed(BigDecimal tradeOwed) {
            this.tradeOwed = tradeOwed;
            return this;
        }

        public Builder fee(Fee fee) {
            this.fees.add(fee);
            return this;
        }

        public Builder rebate(Rebate rebate) {
            this.rebates.add(rebate);
            return this;
        }

        public LoanCalculator build() {
            return new LoanCalculator(this);
        }
        
    }

}
