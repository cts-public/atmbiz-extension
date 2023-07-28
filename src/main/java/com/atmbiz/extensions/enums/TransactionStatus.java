package com.atmbiz.extensions.enums;

public enum TransactionStatus {
    PAYMENT_REQUESTED(0),
    PAYMENT_ARRIVING(1),
    ERROR(2),
    PAYMENT_ARRIVED(3),
    UNKNOWN_STATUS(-1);

    private int value;

    TransactionStatus(int value) {
        this.value = value;
    }

    public static TransactionStatus fromInt(int i) {
        for (TransactionStatus status : values()) {
            if (status.value == i) {
                return status;
            }
        }
        return UNKNOWN_STATUS;
    }
}