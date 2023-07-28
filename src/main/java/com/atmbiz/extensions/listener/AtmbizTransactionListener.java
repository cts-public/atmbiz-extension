package com.atmbiz.extensions.listener;

import com.atmbiz.extensions.AtmbizExtension;
import com.atmbiz.extensions.dao.AtmbizUpdateTransactionRequest;
import com.atmbiz.extensions.enums.TransactionErrorCode;
import com.atmbiz.extensions.enums.TransactionStatus;
import com.atmbiz.extensions.utils.HmacValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

public class AtmbizTransactionListener implements ITransactionListener {
    private static final Logger log = LoggerFactory.getLogger(AtmbizTransactionListener.class);
    private static final String API_KEY = AtmbizExtension.getExtensionContext().getConfigProperty("atmbiz", "API_KEY", "DEFAULT");
    private static final String API_SECRET = AtmbizExtension.getExtensionContext().getConfigProperty("atmbiz", "API_SECRET", "DEFAULT");
    private static final String TRANSACTION_API_URL = "https://atm.biz/api/transactions/updateTransaction";

    @Override
    public boolean isTransactionPreparationApproved(ITransactionPreparation preparation) {
        logTransactionEvent("Transaction isTransactionPreparationApproved");
        return true;
    }

    @Override
    public boolean isTransactionApproved(ITransactionRequest request) {
        logTransactionEvent("Transaction isTransactionApproved");
        return true;
    }

    @Override
    public OutputQueueInsertConfig overrideOutputQueueInsertConfig(ITransactionQueueRequest request, OutputQueueInsertConfig config) {
        logTransactionEvent("Transaction overrideOutputQueueInsertConfig");
        return config;
    }

    @Override
    public Map<String, String> onTransactionCreated(ITransactionDetails details) {
        sendUpdateTransaction(details);
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> onTransactionUpdated(ITransactionDetails details) {
        sendUpdateTransaction(details);
        return Collections.emptyMap();
    }

    @Override
    public void receiptSent(IReceiptDetails receiptDetails) {
    }

    private void logTransactionEvent(String message) {
        log.info(message);
    }

    private void sendUpdateTransaction(ITransactionDetails details) {
        Client client = ClientBuilder.newClient();
        logTransactionEvent("Calling AtmBiz");

        AtmbizUpdateTransactionRequest request = createTransactionRequest(details);

        try {
            sendRequest(client, request);
        } catch (Exception e) {
            log.error("Could not send request {}", e.toString());
        }
    }

    private AtmbizUpdateTransactionRequest createTransactionRequest(ITransactionDetails details) {
        AtmbizUpdateTransactionRequest request = new AtmbizUpdateTransactionRequest();
        request.setRemoteTransactionId(details.getRemoteTransactionId());
        request.setStatus(TransactionStatus.fromInt(details.getStatus()).toString());
        request.setErrorCode(TransactionErrorCode.fromInt(details.getErrorCode()).toString());
        request.setUpdateEvent(details);

        if (details.isWithdrawn()) {
            request.setStatus("WITHDRAWN");
        }

        return request;
    }

    private void sendRequest(Client client, AtmbizUpdateTransactionRequest request) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);
        String hmac = HmacValidator.calculateHmac(json, API_SECRET);

        client.target(TRANSACTION_API_URL)
                .request(MediaType.APPLICATION_JSON)
                .header("X-API-KEY", API_KEY)
                .header("X-SIGNATURE", hmac)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
    }
}
