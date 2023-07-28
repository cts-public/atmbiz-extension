package com.atmbiz.extensions.utils;

import com.atmbiz.extensions.AtmbizExtension;
import com.atmbiz.extensions.dao.AtmbizTerminal;
import com.atmbiz.extensions.dao.OpeningHours;
import com.generalbytes.batm.server.extensions.IAmountWithDiscount;
import com.generalbytes.batm.server.extensions.ICryptoConfiguration;
import com.generalbytes.batm.server.extensions.ILocationDetail;
import com.generalbytes.batm.server.extensions.ITerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TransformUtils {
    protected static final Logger log = LoggerFactory.getLogger(TransformUtils.class);

    private TransformUtils() {
    }

    public static AtmbizTerminal getAtmbizTerminal(ITerminal terminal) {
        AtmbizTerminal atmBizTerminal = new AtmbizTerminal();

        setTerminalDetails(terminal, atmBizTerminal);
        setLocationDetails(terminal, atmBizTerminal);
        setCryptoConfigurationsAndAmounts(terminal, atmBizTerminal);
        setBanknotes(terminal, atmBizTerminal);

        return atmBizTerminal;
    }

    private static void setTerminalDetails(ITerminal terminal, AtmbizTerminal atmBizTerminal) {
        atmBizTerminal.setActive(terminal.isActive());
        atmBizTerminal.setAllowedCashCurrencies(terminal.getAllowedCashCurrencies());
        atmBizTerminal.setDeleted(terminal.isDeleted());
        atmBizTerminal.setErrors(terminal.getErrors());
        atmBizTerminal.setConnectedAt(terminal.getConnectedAt());
        atmBizTerminal.setAllowedCryptoCurrencies(terminal.getAllowedCryptoCurrencies());
        atmBizTerminal.setExchangeRatesBuy(terminal.getExchangeRatesBuy());
        atmBizTerminal.setExchangeRatesSell(terminal.getExchangeRatesSell());
        atmBizTerminal.setLocation(terminal.getLocation());
        atmBizTerminal.setLocked(terminal.isLocked());
        atmBizTerminal.setType(terminal.getType());
        atmBizTerminal.setSerialNumber(terminal.getSerialNumber());
        atmBizTerminal.setName(terminal.getName());
        atmBizTerminal.setLastPingAt(terminal.getLastPingAt());
        atmBizTerminal.setLastPingDuration(terminal.getLastPingDuration());
        atmBizTerminal.setExchangeRateUpdatedAt(terminal.getExchangeRateUpdatedAt());
        atmBizTerminal.setOperationalMode(terminal.getOperationalMode());
        atmBizTerminal.setRejectedReason(terminal.getRejectedReason());
    }

    private static void setLocationDetails(ITerminal terminal, AtmbizTerminal atmBizTerminal) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        ILocationDetail locationDetail = AtmbizExtension.getExtensionContext().getLocationByPublicId(terminal.getLocation().getPublicId());
        atmBizTerminal.setOpeningHours(locationDetail.getOpeningHours().stream().map(iOpeningHours -> {
            OpeningHours openingHours = new OpeningHours();
            openingHours.setDay(iOpeningHours.getDay());
            openingHours.setOrder(iOpeningHours.getDay().ordinal());
            openingHours.setFrom(iOpeningHours.getFrom() != null? formatter.format(iOpeningHours.getFrom()) : null);
            openingHours.setTo(iOpeningHours.getTo() != null? formatter.format(iOpeningHours.getTo()) : null);
            return openingHours;
        }).collect(Collectors.toList()));
    }

    private static void setCryptoConfigurationsAndAmounts(ITerminal terminal, AtmbizTerminal atmBizTerminal) {
        List<ICryptoConfiguration> cryptoConfiguration = AtmbizExtension.getExtensionContext().findCryptoConfigurationsByTerminalSerialNumbers(Arrays.asList(terminal.getSerialNumber()));
        atmBizTerminal.setCryptoConfigurations(cryptoConfiguration);

        log.warn(terminal.getSerialNumber());
        log.warn(terminal.getAllowedCryptoCurrencies().toString());
        try {
            Map<String, IAmountWithDiscount> cryptoAmounts = AtmbizExtension.getExtensionContext().calculateCryptoAmounts(terminal.getSerialNumber(), terminal.getAllowedCryptoCurrencies(), BigDecimal.valueOf(1000), terminal.getAllowedCashCurrencies().get(0), 4, null, null);
            atmBizTerminal.setCryptoAmounts(cryptoAmounts);
            atmBizTerminal.setCryptoAmountsStatus("LOADED");
        } catch (Exception e) {
            atmBizTerminal.setCryptoAmountsStatus("FAILED_TO_LOAD");
        }
    }

    private static void setBanknotes(ITerminal terminal, AtmbizTerminal atmBizTerminal) {
        Map<String, Map<BigDecimal, Integer>> banknotes = new HashMap<>();
        for(String currency: terminal.getAllowedCashCurrencies()){
            banknotes.put(currency, AtmbizExtension.getExtensionContext().getAvailableBanknotesConsideringFutureWithdrawals(terminal.getSerialNumber(), currency));
        }
        atmBizTerminal.setBanknotes(banknotes);
    }
}