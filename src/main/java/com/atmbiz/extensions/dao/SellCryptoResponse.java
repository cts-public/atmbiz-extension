package com.atmbiz.extensions.dao;

public class SellCryptoResponse {
    private int code;
    private String errorCode;
    private String errorMessage;
    private Object data;

    public SellCryptoResponse(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public SellCryptoResponse(int code,String errorCode, String errorMessage) {
        this.code = code;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
