package com.atmbiz.extensions.dao;

public class ErrorMessage extends Exception{
    private String status;
    private String message;

    public ErrorMessage(String status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public ErrorMessage(String status, String message, Throwable e) {
        super(message, e);
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                + "\"status\": \"" + status + "\","
                + "\"message\": \"" + message + "\""
                + "}";
    }
}