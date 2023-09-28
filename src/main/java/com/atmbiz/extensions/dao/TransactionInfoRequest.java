package com.atmbiz.extensions.dao;

import java.util.List;

public class TransactionInfoRequest {
    private List<String> transactionIds;
    private String oswApiKey;

    public String getOswApiKey() {
        return oswApiKey;
    }

    public void setOswApiKey(String oswApiKey) {
        this.oswApiKey = oswApiKey;
    }

    public List<String> getTransactionIds() {
        return transactionIds;
    }

    public void setTransactionIds(List<String> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
