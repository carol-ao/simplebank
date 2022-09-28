package com.carol.simplebank.factory;

import com.carol.simplebank.model.Account;

public class AccountFactory {

  public static Account getAccountWithBigValueBalanceForUser1() {
    return Account.builder().id(1L).user(UserFactory.getUser1()).balance(2000.00).build();
  }

  public static Account getAccountWithSmallValueBalanceForUser2() {
    return Account.builder().id(2L).user(UserFactory.getUser2()).balance(1500.00).build();
  }

  public static Account getEmptyAccountForUser1() {
    return Account.builder().id(1L).user(UserFactory.getUser1()).balance(0).build();
  }
}
