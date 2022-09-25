package com.carol.simplebank.exceptions;

public class DuplicateAccountException extends Exception {
    public DuplicateAccountException(String msg) {
      super(msg);
    }
}
