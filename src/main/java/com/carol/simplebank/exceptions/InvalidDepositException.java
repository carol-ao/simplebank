package com.carol.simplebank.exceptions;

public class InvalidDepositException extends Exception {
    public InvalidDepositException(String msg) {
        super(msg);
    }
}
