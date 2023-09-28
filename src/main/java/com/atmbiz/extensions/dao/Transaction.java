package com.atmbiz.extensions.dao;

public class Transaction {
    private String status;
    private String errorCode;
    private String remoteTransactionId;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemoteTransactionId() {
        return remoteTransactionId;
    }

    public void setRemoteTransactionId(String remoteTransactionId) {
        this.remoteTransactionId = remoteTransactionId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
