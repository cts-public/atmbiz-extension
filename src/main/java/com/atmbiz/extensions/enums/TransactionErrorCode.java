package com.atmbiz.extensions.enums;

public enum TransactionErrorCode {
    INVALID_PARAMETERS(1),
    INVALID_CURRENCY(2),
    INVALID_BALANCE(3),
    INVALID_UNKNOWN_ERROR(4),
    CONFIGURATION_PROBLEM(11),
    FINGERPRINT_UNKNOWN(12),
    GETTING_DEPOSIT_ADDRESS(13),
    PAYMENT_WAIT_TIMED_OUT(14),
    NOT_ENOUGH_COINS_ON_EXCHANGE(15),
    EXCHANGE_SELL(16),
    PAYMENT_INVALID(17),
    DISABLED_SELL(20),
    NOT_APPROVED(21),
    WITHDRAWAL_PROBLEM(22),
    WITHDRAWAL_NOT_ALLOWED(23),
    UNKNOWN_ERROR(-1);

    private int value;

    TransactionErrorCode(int value) {
        this.value = value;
    }

    public static TransactionErrorCode fromInt(int i) {
        for (TransactionErrorCode error : values()) {
            if (error.value == i) {
                return error;
            }
        }
        return UNKNOWN_ERROR;
    }
}