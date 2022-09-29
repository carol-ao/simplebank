package com.carol.simplebank.dto;

import com.carol.simplebank.model.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {

  private Long id;
  private Long userId;
  private double balance;

  public AccountDto(Account account) {
    this.id = account.getId();
     this.balance = account.getBalance();
    this.userId = account.getUser().getId();
  }
}
