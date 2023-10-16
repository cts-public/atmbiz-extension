package com.atmbiz.extensions.controller;

import com.atmbiz.extensions.AtmbizExtension;
import com.atmbiz.extensions.dao.*;
import com.atmbiz.extensions.enums.TransactionErrorCode;
import com.atmbiz.extensions.enums.TransactionStatus;
import com.atmbiz.extensions.utils.TransformUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class AtmbizRPCController {
    private static final Logger log = LoggerFactory.getLogger(AtmbizRPCController.class);

    public static String terminalsRpc(String oswApiKey) {
        log.info("oswApiKey:"+oswApiKey);
        if (oswApiKey == null || oswApiKey.isEmpty()) {
            return "{\"status\":\"error\",\"message\":\"API key is mandatory\"}";
        }

        try {
            IApiAccess iApiAccess = AtmbizExtension.getExtensionContext().getAPIAccessByKey(oswApiKey, ApiAccessType.OSW);
            if (iApiAccess != null) {
                List<AtmbizTerminal> terminals = getTerminalsByApiKey(iApiAccess);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(terminals);
            } else {
                return "{\"status\":\"error\",\"message\":\"Unauthorized response: Invalid API key\"}";
            }
        } catch (Exception e) {
            log.error("Error", e);
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    private static List<AtmbizTerminal> getTerminalsByApiKey(IApiAccess iApiAccess) {
        Collection<String> terminals = iApiAccess.getTerminalSerialNumbers();
        List<AtmbizTerminal> filteredTerminals = new ArrayList<>();
        terminals.forEach(terminalSerial -> {
            ITerminal terminal = AtmbizExtension.getExtensionContext().findTerminalBySerialNumber(terminalSerial);
            if (terminal != null && !terminal.isDeleted()) {
                filteredTerminals.add(TransformUtils.getAtmbizTerminal(terminal));
            }
        });
        return filteredTerminals;
    }

    public static String sellCryptoRpc(String message) {
        ObjectMapper mapper = new ObjectMapper();

        // Deserialize the request from the message string
        AtmbizSellCryptoRequest requestObject;
        try {
            requestObject = mapper.readValue(message, AtmbizSellCryptoRequest.class);
        } catch (IOException e) {
            return "{\"status\":\"error\",\"message\":\"Invalid request format\"}";
        }

        // Validate if serialNumber in request object is valid against given api_key
        if (!validateSerialNumber(requestObject.getOswApiKey(), requestObject.getSerialNumber())) {
            return "{\"status\":\"error\",\"message\":\"Invalid Serial Number for the given API Key\"}";
        }

        // Use the JSON string as the input for your HMAC calculation
        String data;
        try {
            data = mapper.writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing JSON", e);
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }

        log.info("Received sellCrypto request with data: {}", data);

        // Validate request object
        if (requestObject.getSerialNumber() == null || requestObject.getFiatAmount() == null || requestObject.getFiatCurrency() == null || requestObject.getCryptoAmount() == null || requestObject.getCryptoCurrency() == null) {
            return "{\"status\":\"error\",\"message\":\"Missing properties!\"}";
        }

        // Create Identity
        IIdentity identity;
        try {
            identity = AtmbizExtension.getExtensionContext().addIdentity(requestObject.getFiatCurrency(), requestObject.getSerialNumber(), null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "note", 4, BigDecimal.ZERO, BigDecimal.ZERO, new Date(), null);
        } catch (Throwable e) {
            log.error("Error occurred while creating identity", e);
            return "{\"status\":\"error\",\"message\":\"Error during identity creation:" + e.getMessage()+"\"}";
        }
        // Add phone and email
        try {
            if (requestObject.getPhoneNumber() != null)
                AtmbizExtension.getExtensionContext().addIdentityPiece(identity.getPublicId(), new IdentityPiece(IIdentityPiece.TYPE_CELLPHONE, null, requestObject.getPhoneNumber()));
            if (requestObject.getEmail() != null)
                AtmbizExtension.getExtensionContext().addIdentityPiece(identity.getPublicId(), new IdentityPiece(IIdentityPiece.TYPE_EMAIL, requestObject.getEmail(), null));
        } catch (Throwable e) {
            log.error("Error occurred while updating identity", e);
            return "{\"status\":\"error\",\"message\":\"Error during identity update:" + e.getMessage()+"\"}";
        }

        BigDecimal avaiableCash = AtmbizExtension.getExtensionContext().calculateCashAvailableForSell(requestObject.getSerialNumber(), requestObject.getFiatCurrency());
        log.info("Available cash is {}", avaiableCash);

        if (avaiableCash.compareTo(requestObject.getFiatAmount()) < 0) {
            log.error("Not enough cash in ATM, available cash is {}", avaiableCash);
            return "{\"status\":\"error\",\"message\":\"There is no enough cash in ATM, available cash is " + avaiableCash+"\"}";
        }

        try {
            SellCryptoResponse response = new SellCryptoResponse(0, AtmbizExtension.getExtensionContext().sellCrypto(requestObject.getSerialNumber(), requestObject.getFiatAmount(), requestObject.getFiatCurrency(), requestObject.getCryptoAmount(), requestObject.getCryptoCurrency(), identity.getPublicId(), requestObject.getDiscountCode() )); // the arguments you were using before
            return mapper.writeValueAsString(response);
        } catch (Throwable e) {
            log.error("Error occurred while selling crypto", e);
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    public static String getTransactionInfo(String message){
        ObjectMapper mapper = new ObjectMapper();

        // Deserialize the request from the message string
        TransactionInfoRequest requestObject;
        try {
            requestObject = mapper.readValue(message, TransactionInfoRequest.class);
        } catch (IOException e) {
            return "{\"status\":\"error\",\"message\":\"Invalid request format\"}";
        }

//        try {
//            IApiAccess iApiAccess = AtmbizExtension.getExtensionContext().getAPIAccessByKey(requestObject.getOswApiKey(), ApiAccessType.OSW);
//            if (iApiAccess != null) {
//                List<AtmbizTerminal> terminals = getTerminalsByApiKey(iApiAccess);
//                return mapper.writeValueAsString(terminals);
//            } else {
//                return "{\"status\":\"error\",\"message\":\"Unauthorized response: Invalid API key\"}";
//            }
//        } catch (Exception e) {
//            log.error("Error", e);
//            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
//        }

        List<Transaction> responseList = new ArrayList<>();

        for(String id: requestObject.getTransactionIds()){
            responseList.add(getTransaction(id));
        }

        try {
            TransactionInfoResponse response = new TransactionInfoResponse();
            response.setTransactions(responseList);
            return mapper.writeValueAsString(response);
        } catch (Throwable e) {
            log.error("Error occurred while getting information about transactions", e);
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    private static Transaction getTransaction(String transactionId){

        ITransactionDetails details = AtmbizExtension.getExtensionContext().findTransactionByTransactionId(transactionId);

        Transaction transaction = new Transaction();
        transaction.setRemoteTransactionId(details.getRemoteTransactionId());
        transaction.setData(details);

        if (details.isWithdrawn()) {
            transaction.setStatus("WITHDRAWN");
            return transaction;
        }

        if (details.getErrorCode() != 0) {
            transaction.setErrorCode(TransactionErrorCode.fromInt(details.getErrorCode()).toString());

            if (TransactionErrorCode.PAYMENT_WAIT_TIMED_OUT.getValue() == details.getErrorCode()) {
                transaction.setStatus(TransactionStatus.EXPIRED.toString());
                return transaction;
            }
        }

        transaction.setStatus(TransactionStatus.fromInt(details.getStatus()).toString());

        return transaction;
    }

    private static boolean validateSerialNumber(String apiKey, String serialNumber) {
        IApiAccess iApiAccess = AtmbizExtension.getExtensionContext().getAPIAccessByKey(apiKey, ApiAccessType.OSW);
        if (iApiAccess != null) {
            Collection<String> terminals = iApiAccess.getTerminalSerialNumbers();
            return terminals.contains(serialNumber);
        }
        return false;
    }
}
