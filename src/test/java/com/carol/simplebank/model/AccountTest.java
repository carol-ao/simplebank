package com.carol.simplebank.model;

import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static com.carol.simplebank.util.Constants.MAX_AMOUNT_ALLOWED_PER_OPERATION;

public class AccountTest {

  @Test
  public void mustDepositValueInEmptyAccountWhenAmountIsPositiveAndNotAboveMaxValue()
      throws InvalidDepositException {
    Account account = getEmptyAccount();

    account.deposit(MAX_AMOUNT_ALLOWED_PER_OPERATION);

    Assertions.assertEquals(MAX_AMOUNT_ALLOWED_PER_OPERATION, account.getBalance());
  }

  @Test
  public void mustDepositValueInNonEmptyAccountWhenAmountIsPositiveAndNotAboveMaxValue()
      throws InvalidDepositException {
    Account account = getAccountWithBigValueBalance(getUser1());
    double expectedBalance = MAX_AMOUNT_ALLOWED_PER_OPERATION + account.getBalance();

    account.deposit(MAX_AMOUNT_ALLOWED_PER_OPERATION);

    Assertions.assertEquals(expectedBalance, account.getBalance());
  }

  @Test
  public void mustThrowInvalidDepositExceptionWhenAmountDepositedAboveMaxValue() {
    Account account = getAccountWithBigValueBalance(getUser1());

    Exception exception =
        Assertions.assertThrows(
            InvalidDepositException.class,
            () -> account.deposit(MAX_AMOUNT_ALLOWED_PER_OPERATION + 100.00));

    Assertions.assertEquals(
        "The deposit amount must not be greater than "
            .concat("R$")
            .concat(String.valueOf(MAX_AMOUNT_ALLOWED_PER_OPERATION))
            .concat(". accountId:".concat(account.getId().toString())),
        exception.getMessage());
  }

  @Test
  public void mustThrowInvalidDepositExceptionWhenAmountDepositedNegative() {
    Account account = getAccountWithBigValueBalance(getUser1());

    Exception exception =
        Assertions.assertThrows(InvalidDepositException.class, () -> account.deposit(-100.00));

    Assertions.assertEquals(
        "The deposit amount must be non-negative. accountId:".concat(account.getId().toString()),
        exception.getMessage());
  }

  @Test
  public void mustThrowInvalidTransferExceptionWhenAmountToTransferIsNegative() {
    Account account = getAccountWithBigValueBalance(getUser1());
    double transferredAmount = -100.00;

    Exception exception =
        Assertions.assertThrows(
            InvalidTransferException.class, () -> account.transfer(transferredAmount));

    Assertions.assertEquals(
        "The transferred amount must be non-negative. accountId:"
            .concat(account.getId().toString()),
        exception.getMessage());
  }

  @Test
  public void mustThrowInvalidTransferExceptionWhenResultingBalanceInOriginAccountWouldBeNegative() {
    Account account = getAccountWithSmallValueBalance(getUser1());
    double transferredAmount = account.getBalance() + 100.00;

    Exception exception =
        Assertions.assertThrows(
            InvalidTransferException.class, () -> account.transfer(transferredAmount));

    Assertions.assertEquals(
            "The transferred amount from an account "
                    + "to another cannot result in negative balance. accountId:"
                    .concat(account.getId().toString()),
        exception.getMessage());
  }

  @Test
  public void mustThrowInvalidReceiveExceptionWhenAmountReceivedIsNegative() {
    Account account = getAccountWithBigValueBalance(getUser1());
    double receivedAmount = -100.00;

    Exception exception =
            Assertions.assertThrows(
                    InvalidTransferException.class, () -> account.receive(receivedAmount));

    Assertions.assertEquals("The amount received in a transfer must be non-negative. accountId:"
            .concat(account.getId().toString()),
            exception.getMessage());
  }

  private User getUser1() {
    return User.builder()
        .id(1L)
        .password("123")
        .cpf("052.468.324-73")
        .name("Milly Alcock")
        .roles(new HashSet<Role>(Collections.singleton(Role.builder().id(1L).build())))
        .build();
  }

  private User getUser2() {
    return User.builder()
        .id(1L)
        .password("456")
        .cpf("856.758.704-23")
        .name("Matt Smith")
        .roles(new HashSet<Role>(Collections.singleton(Role.builder().id(1L).build())))
        .build();
  }

  private Account getEmptyAccount() {
    return Account.builder().id(1L).user(getUser1()).balance(0).build();
  }

  private Account getAccountWithBigValueBalance(User user) {
    return Account.builder().id(1L).user(user).balance(2000.00).build();
  }

  private Account getAccountWithSmallValueBalance(User user) {
    return Account.builder().id(2L).user(user).balance(1500.00).build();
  }
}
