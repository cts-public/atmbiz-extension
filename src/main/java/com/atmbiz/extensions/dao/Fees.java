package com.atmbiz.extensions.dao;

import java.math.BigDecimal;

public class Fees {
    BigDecimal buyFeePercentage;
    BigDecimal sellFeePercentage;
    BigDecimal fixedTransactionFee;
    String cryptoCurrency;

    public BigDecimal getBuyFeePercentage() {
        return buyFeePercentage;
    }

    public void setBuyFeePercentage(BigDecimal buyFeePercentage) {
        this.buyFeePercentage = buyFeePercentage;
    }

    public BigDecimal getSellFeePercentage() {
        return sellFeePercentage;
    }

    public void setSellFeePercentage(BigDecimal sellFeePercentage) {
        this.sellFeePercentage = sellFeePercentage;
    }

    public BigDecimal getFixedTransactionFee() {
        return fixedTransactionFee;
    }

    public void setFixedTransactionFee(BigDecimal fixedTransactionFee) {
        this.fixedTransactionFee = fixedTransactionFee;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(String cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }
}
