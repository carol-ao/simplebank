package com.carol.simplebank.model;

import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;

import javax.persistence.Entity;

import static com.carol.simplebank.util.Constants.MAX_AMOUNT_ALLOWED_PER_DEPOSIT;
import static com.carol.simplebank.util.Constants.MAX_AMOUNT_ALLOWED_PER_TRANSFER;

@Entity
public class Account extends BasicAccount {

  @Override
  public void deposit(double amount) throws InvalidDepositException {

    if (amount < 0) {
      throw new InvalidDepositException("The deposit amount must be non-negative");
    }
    if (amount > MAX_AMOUNT_ALLOWED_PER_DEPOSIT) {
      throw new InvalidDepositException(
          "For this account, the deposit amount must not be greater than "
              .concat("R$")
              .concat(String.valueOf(MAX_AMOUNT_ALLOWED_PER_DEPOSIT))
              .concat("."));
    }
  }

  @Override
  public void transfer(double amount) throws InvalidTransferException {
    if (amount < 0) {
      throw new InvalidTransferException("The deposit amount must be non-negative");
    }
    if (amount > MAX_AMOUNT_ALLOWED_PER_TRANSFER) {
      throw new InvalidTransferException(
          "For this account, the transfered amount must not be greater than "
              .concat("R$")
              .concat(String.valueOf(MAX_AMOUNT_ALLOWED_PER_TRANSFER))
              .concat("."));
    }

    if (this.balance - amount < 0) {
      // TODO: continue here
    }
  }
}
