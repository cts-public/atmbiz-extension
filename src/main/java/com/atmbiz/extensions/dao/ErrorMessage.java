package com.atmbiz.extensions.dao;

public class ErrorMessage extends Exception{
    private String code;
    private String message;

    public ErrorMessage(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ErrorMessage(String code, String message, Throwable e) {
        super(message, e);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{"
                + "\"code\": \"" + code + "\","
                + "\"message\": \"" + message + "\""
                + "}";
    }
}