package com.carol.simplebank.model;

import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.carol.simplebank.util.Constants.MAX_AMOUNT_ALLOWED_PER_OPERATION;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne protected User user;

  protected double balance; // TODO: change to BigDecimal

  public void deposit(double amount) throws InvalidDepositException {

    if (amount < 0) {
      throw new InvalidDepositException("The deposit amount must be non-negative.");
    }
    if (amount > MAX_AMOUNT_ALLOWED_PER_OPERATION) {
      throw new InvalidDepositException(
          "The deposit amount must not be greater than "
              .concat("R$")
              .concat(String.valueOf(MAX_AMOUNT_ALLOWED_PER_OPERATION))
              .concat("."));
    }

    this.balance += amount;
  }

  public void transfer(double amount) throws InvalidTransferException {
    if (amount < 0) {
      throw new InvalidTransferException(
          "The transferred amount into an account must be non-negative");
    }
    if (amount > MAX_AMOUNT_ALLOWED_PER_OPERATION) {
      throw new InvalidTransferException(
          "The transferred amount from an account "
              + "to another cannot result in negative balance.");
    }
    this.balance -= amount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public double getBalance() {
    return balance;
  }
}
