package com.carol.simplebank.model;

import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

public abstract class BasicAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @OneToOne protected User user;

  protected double balance; // TODO: change to BigDecimal

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

  public abstract void deposit(double amount) throws InvalidDepositException;

  public abstract void transfer(double amount) throws InvalidTransferException;
}
