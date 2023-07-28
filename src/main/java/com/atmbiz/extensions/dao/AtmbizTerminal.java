package com.atmbiz.extensions.dao;


import com.generalbytes.batm.server.extensions.IAmountWithDiscount;
import com.generalbytes.batm.server.extensions.ICryptoConfiguration;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AtmbizTerminal {
    Integer type;
    String serialNumber;
    String name;
    boolean isActive;
    boolean isLocked;
    boolean isDeleted;
    Object location;
    Date connectedAt;
    Date lastPingAt;
    Long lastPingDuration;
    Date exchangeRateUpdatedAt;
    String exchangeRatesBuy;
    String exchangeRatesSell;
    long errors;
    int operationalMode;
    int rejectedReason;
    List<String> allowedCashCurrencies;
    List<String> allowedCryptoCurrencies;
    List<ICryptoConfiguration> cryptoConfigurations;
    List<OpeningHours> openingHours;
    Map<String, IAmountWithDiscount> cryptoAmounts;
    String cryptoAmountsStatus;
    Map<String,Map<BigDecimal, Integer>> banknotes;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Date getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(Date connectedAt) {
        this.connectedAt = connectedAt;
    }

    public Date getLastPingAt() {
        return lastPingAt;
    }

    public void setLastPingAt(Date lastPingAt) {
        this.lastPingAt = lastPingAt;
    }

    public Long getLastPingDuration() {
        return lastPingDuration;
    }

    public void setLastPingDuration(Long lastPingDuration) {
        this.lastPingDuration = lastPingDuration;
    }

    public Date getExchangeRateUpdatedAt() {
        return exchangeRateUpdatedAt;
    }

    public void setExchangeRateUpdatedAt(Date exchangeRateUpdatedAt) {
        this.exchangeRateUpdatedAt = exchangeRateUpdatedAt;
    }

    public String getExchangeRatesBuy() {
        return exchangeRatesBuy;
    }

    public void setExchangeRatesBuy(String exchangeRatesBuy) {
        this.exchangeRatesBuy = exchangeRatesBuy;
    }

    public String getExchangeRatesSell() {
        return exchangeRatesSell;
    }

    public void setExchangeRatesSell(String exchangeRatesSell) {
        this.exchangeRatesSell = exchangeRatesSell;
    }

    public long getErrors() {
        return errors;
    }

    public void setErrors(long errors) {
        this.errors = errors;
    }

    public int getOperationalMode() {
        return operationalMode;
    }

    public void setOperationalMode(int operationalMode) {
        this.operationalMode = operationalMode;
    }

    public int getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(int rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public List<String> getAllowedCashCurrencies() {
        return allowedCashCurrencies;
    }

    public void setAllowedCashCurrencies(List<String> allowedCashCurrencies) {
        this.allowedCashCurrencies = allowedCashCurrencies;
    }

    public List<String> getAllowedCryptoCurrencies() {
        return allowedCryptoCurrencies;
    }

    public void setAllowedCryptoCurrencies(List<String> allowedCryptoCurrencies) {
        this.allowedCryptoCurrencies = allowedCryptoCurrencies;
    }

    public List<OpeningHours> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<OpeningHours> openingHours) {
        this.openingHours = openingHours;
    }

    public List<ICryptoConfiguration> getCryptoConfigurations() {
        return cryptoConfigurations;
    }

    public void setCryptoConfigurations(List<ICryptoConfiguration> cryptoConfigurations) {
        this.cryptoConfigurations = cryptoConfigurations;
    }

    public Map<String, IAmountWithDiscount> getCryptoAmounts() {
        return cryptoAmounts;
    }

    public void setCryptoAmounts(Map<String, IAmountWithDiscount> cryptoAmounts) {
        this.cryptoAmounts = cryptoAmounts;
    }

    public String getCryptoAmountsStatus() {
        return cryptoAmountsStatus;
    }

    public void setCryptoAmountsStatus(String cryptoAmountsStatus) {
        this.cryptoAmountsStatus = cryptoAmountsStatus;
    }

    public Map<String, Map<BigDecimal, Integer>> getBanknotes() {
        return banknotes;
    }

    public void setBanknotes(Map<String, Map<BigDecimal, Integer>> banknotes) {
        this.banknotes = banknotes;
    }
}
