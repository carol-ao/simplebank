package com.carol.simplebank.service.account;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;

import javax.transaction.Transactional;

public interface AccountService {

  // ADMIN OPERATIONS

  @Transactional
  AccountDto openAccount(Long userId) throws ResourceNotFoundException, DuplicateAccountException;

  AccountDto findAccountByUserName(String userName) throws ResourceNotFoundException;

  AccountDto findById(Long id) throws ResourceNotFoundException;

  @Transactional
  AccountDto deposit(Long id, double sum) throws ResourceNotFoundException, InvalidDepositException;

  // BANK ACCOUNT OWNER OPERATIONS

  AccountDto consultAccount() throws ResourceNotFoundException;

  @Transactional
  AccountDto transfer(Long destinationAccountId, double amount) throws ResourceNotFoundException, InvalidTransferException;
}
