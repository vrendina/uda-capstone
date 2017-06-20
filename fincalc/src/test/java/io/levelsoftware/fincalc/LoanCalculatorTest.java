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

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import io.levelsoftware.fincalc.list.Fee;
import io.levelsoftware.fincalc.list.Rebate;

import static org.junit.Assert.assertEquals;

public class LoanCalculatorTest {

    LoanCalculator calculator;

    @Before
    public void setUp() throws Exception {
        BigDecimal price = new BigDecimal("10000");
        BigDecimal interestPercentage = new BigDecimal("3.79");
        Integer term = 72;

        calculator = new LoanCalculator.Builder(price, interestPercentage, term)
                .downPayment(new BigDecimal("500"))
                .tradeValue(new BigDecimal("3000"))
                .tradeOwed(new BigDecimal("379.59"))
                .rebate(new Rebate(new BigDecimal("1000"), true))
                .rebate(new Rebate(new BigDecimal("500"), false))
                .fee(new Fee(new BigDecimal("500"), true, true))
                .fee(new Fee(new BigDecimal("250"), true, false))
                .fee(new Fee(new BigDecimal("100"), false, false))
                .tax(new BigDecimal("5.25"), true)
                .build();
    }

    @Test
    public void getTotalCost() throws Exception {
        assertEquals(new BigDecimal("8050.33"),
                calculator.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        calculator.setTerm(0);

        assertEquals(new BigDecimal("7254.59"),
                calculator.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        calculator.setTerm(72);
        calculator.setInterestPercentage(BigDecimal.ZERO);

        assertEquals(new BigDecimal("7254.59"),
                calculator.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void getMonthlyPayment() throws Exception {
        assertEquals(new BigDecimal("103.48"),
                calculator.getMonthlyPayment().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        calculator.setTerm(0);
        assertEquals(new BigDecimal("0"), calculator.getMonthlyPayment().stripTrailingZeros());
        assertEquals(new BigDecimal("0"), calculator.getTotalInterest().stripTrailingZeros());
        assertEquals(new BigDecimal("6654.59"),
                calculator.getTotalLoanCost().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        calculator.setTerm(32);
        assertEquals(new BigDecimal("218.97"),
                calculator.getMonthlyPayment().setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(new BigDecimal("7007.03"),
                calculator.getTotalLoanCost().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        calculator.setInterestPercentage(BigDecimal.ZERO);

        assertEquals(new BigDecimal("207.96"),
                calculator.getMonthlyPayment().setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(new BigDecimal("6654.59"),
                calculator.getTotalLoanCost().setScale(2, BigDecimal.ROUND_HALF_EVEN));

    }

    @Test
    public void getDueAtSigning() throws Exception {
        assertEquals(new BigDecimal("600.00"),
                calculator.getDueAtSigning().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        calculator.setTaxCapitalized(false);

        assertEquals(new BigDecimal("1125.00"),
                calculator.getDueAtSigning().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        assertEquals(new BigDecimal("732.96"),
                calculator.getTotalInterest().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        calculator.setTerm(0);

        assertEquals(new BigDecimal("7254.59"),
                calculator.getDueAtSigning().setScale(2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void getNetCapitalizedCost() {
        assertEquals(new BigDecimal("6654.59"), calculator.getNetCapitalizedCost().stripTrailingZeros());
    }

    @Test
    public void getCapitalizedVehicle() {
        assertEquals(new BigDecimal("5379.59"), calculator.getCapitalizedVehicle());
    }

    @Test
    public void getCapitalizedTax() {
        assertEquals(new BigDecimal("525"), calculator.getCapitalizedTax().stripTrailingZeros());

        calculator.setTaxCapitalized(false);
        assertEquals(BigDecimal.ZERO, calculator.getCapitalizedTax());
    }

    @Test
    public void getVehicleTaxBasis() throws Exception {
        assertEquals(new BigDecimal("9500"), calculator.getVehicleTaxBasis());
    }

    @Test
    public void getVehicleTax() throws Exception {
        assertEquals(new BigDecimal("498.75"), calculator.getVehicleTax().stripTrailingZeros());
    }

    @Test
    public void getFeeTax() throws Exception {
        assertEquals(new BigDecimal("26.25"), calculator.getFeeTax().stripTrailingZeros());
    }

    @Test
    public void getTotalTax() throws Exception {
        assertEquals(new BigDecimal("525"), calculator.getTotalTax().stripTrailingZeros());
    }

    @Test
    public void getTaxableFees() {
        assertEquals(new BigDecimal("500"), calculator.getTaxableFees());
    }

    @Test
    public void getNonTaxableRebates() {
        assertEquals(new BigDecimal("500"), calculator.getNonTaxableRebates());
    }

    @Test
    public void getPeriodicInterestRate() {
        assertEquals(new BigDecimal("0.00315833"),
                calculator.getPeriodicInterestRate().setScale(8, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void getAmortizationFactor() {
        assertEquals(new BigDecimal("0.015549681725"),
                calculator.getAmortizationCoefficient().setScale(12, BigDecimal.ROUND_HALF_EVEN));
    }

    @Test
    public void getPrincipalCredit() {
        calculator.setTradeValue("11000");
        assertEquals(new BigDecimal("-745.41"),
                calculator.getTotalCost().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        assertEquals(new BigDecimal("-745.41"),
                calculator.getDueAtSigning().setScale(2, BigDecimal.ROUND_HALF_EVEN));

        assertEquals(BigDecimal.ZERO,
                calculator.getMonthlyPayment().stripTrailingZeros());
    }

}