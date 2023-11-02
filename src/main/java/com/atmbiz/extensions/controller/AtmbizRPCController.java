package com.atmbiz.extensions.controller;

import com.atmbiz.extensions.AtmbizExtension;
import com.atmbiz.extensions.dao.ErrorMessage;
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

    public static String terminalsRpc(String oswApiKey) throws ErrorMessage {
        if (oswApiKey == null || oswApiKey.isEmpty()) {
            throw new ErrorMessage("error","API key is mandatory");
        }

        try {
            IApiAccess iApiAccess = AtmbizExtension.getExtensionContext().getAPIAccessByKey(oswApiKey, ApiAccessType.OSW);
            if (iApiAccess != null) {
                List<AtmbizTerminal> terminals = getTerminalsByApiKey(iApiAccess);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(terminals);
            } else {
                throw new ErrorMessage("error","Unauthorized response: Invalid API key");
            }
        } catch (Exception e) {
            log.error("Error", e);
            throw new ErrorMessage("error",e.getMessage(), e);
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

    public static String sellCryptoRpc(String message) throws ErrorMessage {
        ObjectMapper mapper = new ObjectMapper();

        // Deserialize the request from the message string
        AtmbizSellCryptoRequest requestObject;
        try {
            requestObject = mapper.readValue(message, AtmbizSellCryptoRequest.class);
        } catch (IOException e) {
            throw new ErrorMessage("INVALID_REQUEST_FORMAT","Invalid request format");
        }

        // Validate if serialNumber in request object is valid against given api_key
        if (!validateSerialNumber(requestObject.getOswApiKey(), requestObject.getSerialNumber())) {
            throw new ErrorMessage("INVALID_API_KEY_PERMISSION","Invalid Serial Number for the given API Key");
        }

        try {
            log.info("Received sellCrypto request with data: {}",  mapper.writeValueAsString(requestObject));
        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing JSON", e);
            throw new ErrorMessage("INVALID_REQUEST",e.getMessage(), e);
        }

        // Validate request object
        if (requestObject.getSerialNumber() == null || requestObject.getFiatAmount() == null || requestObject.getFiatCurrency() == null || requestObject.getCryptoAmount() == null || requestObject.getCryptoCurrency() == null) {
            throw new ErrorMessage("INVALID_REQUEST","Missing properties!");
        }

        // Create Identity
        IIdentity identity;
        try {
            identity = AtmbizExtension.getExtensionContext().addIdentity(requestObject.getFiatCurrency(), requestObject.getSerialNumber(), null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "note", 4, BigDecimal.ZERO, BigDecimal.ZERO, new Date(), null);
        } catch (Exception e) {
            log.error("Error occurred while creating identity", e);
            throw new ErrorMessage("IDENTITY_CREATE","Error during identity creation:" + e.getMessage(), e);
        }
        // Add phone and email
        try {
            if (requestObject.getPhoneNumber() != null)
                AtmbizExtension.getExtensionContext().addIdentityPiece(identity.getPublicId(), new IdentityPiece(IIdentityPiece.TYPE_CELLPHONE, null, requestObject.getPhoneNumber()));
            if (requestObject.getEmail() != null)
                AtmbizExtension.getExtensionContext().addIdentityPiece(identity.getPublicId(), new IdentityPiece(IIdentityPiece.TYPE_EMAIL, requestObject.getEmail(), null));
        } catch (Exception e) {
            log.error("Error occurred while updating identity", e);
            throw new ErrorMessage("IDENTITY_UPDATE","Error during identity update:" + e.getMessage(), e);
        }

        BigDecimal availableCash = AtmbizExtension.getExtensionContext().calculateCashAvailableForSell(requestObject.getSerialNumber(), requestObject.getFiatCurrency());
        log.info("Available cash is {}", availableCash);

        if (availableCash.compareTo(requestObject.getFiatAmount()) < 0) {
            log.error("Not enough cash in ATM, available cash is {}", availableCash);
            throw new ErrorMessage("NOT_ENOUGH_CASH","There is no enough cash in ATM, available cash is " + availableCash);
        }

        try {
            SellCryptoResponse response = new SellCryptoResponse(0, AtmbizExtension.getExtensionContext().sellCrypto(requestObject.getSerialNumber(), requestObject.getFiatAmount(), requestObject.getFiatCurrency(), requestObject.getCryptoAmount(), requestObject.getCryptoCurrency(), identity.getPublicId(), requestObject.getDiscountCode() )); // the arguments you were using before
            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Error occurred while selling crypto", e);
            throw new ErrorMessage("ERROR",e.getMessage(), e);
        }
    }

    public static String getTransactionInfo(String message) throws ErrorMessage {
        ObjectMapper mapper = new ObjectMapper();

        // Deserialize the request from the message string
        TransactionInfoRequest requestObject;
        try {
            requestObject = mapper.readValue(message, TransactionInfoRequest.class);
        } catch (IOException e) {
            throw new ErrorMessage("INVALID_REQUEST",e.getMessage(), e);
        }

        try {
            IApiAccess iApiAccess = AtmbizExtension.getExtensionContext().getAPIAccessByKey(requestObject.getOswApiKey(), ApiAccessType.OSW);
            if (iApiAccess != null) {
                Collection<String> terminals = iApiAccess.getTerminalSerialNumbers();

                if(terminals == null || terminals.isEmpty()){
                    log.error("No terminals assigned to API key");
                    throw new ErrorMessage("INVALID_API_KEY_PERMISSION","No terminals assigned to API key");
                }

                List<Transaction> responseList = new ArrayList<>();

                for(String id: requestObject.getTransactionIds()){
                    ITransactionDetails details = AtmbizExtension.getExtensionContext().findTransactionByTransactionId(id);
                    if(terminals.contains(details.getTerminalSerialNumber())){
                        responseList.add(getTransaction(details));
                    }
                }

                try {
                    TransactionInfoResponse response = new TransactionInfoResponse();
                    response.setTransactions(responseList);
                    return mapper.writeValueAsString(response);
                } catch (Exception e) {
                    log.error("Error occurred while getting information about transactions", e);
                    throw new ErrorMessage("ERROR",e.getMessage(), e);
                }
            } else {
                throw new ErrorMessage("INVALID_API_KEY","Unauthorized response: Invalid API key");
            }
        } catch (Exception e) {
            log.error("Error", e);
            throw new ErrorMessage("error",e.getMessage(), e);
        }
    }

    private static Transaction getTransaction(ITransactionDetails details){
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
