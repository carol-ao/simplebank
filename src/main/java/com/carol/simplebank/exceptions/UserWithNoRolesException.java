package com.carol.simplebank.exceptions;

public class UserWithNoRolesException extends Exception {
    public UserWithNoRolesException(String msg) {
        super(msg);
    }
}
