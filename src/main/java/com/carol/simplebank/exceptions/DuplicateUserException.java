package com.carol.simplebank.exceptions;

public class DuplicateUserException extends Exception {
    public DuplicateUserException(String msg) {
        super(msg);
    }
}
