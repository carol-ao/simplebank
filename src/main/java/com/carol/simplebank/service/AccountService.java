package com.carol.simplebank.service;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.model.Account;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  @Autowired private AccountRepository accountRepository;
  @Autowired private UserService userService;

  public AccountDto findByUserId(Long userId) throws ResourceNotFoundException {

    Account account =
        accountRepository
            .findByUserId(userId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No account for this user was found. userId:"
                            .concat(String.valueOf(userId))));

    return new AccountDto(account);
  }

  public AccountDto openAccount(Long userId)
      throws ResourceNotFoundException, DuplicateAccountException {
    User user = userService.findEntityById(userId);
    if (accountRepository.existsByUserId(userId)) {
      throw new DuplicateAccountException(
          "User already has an account and cannot open a second one. userId:"
              .concat(String.valueOf(userId)));
    }
    Account newAccount = Account.builder().user(user).build();
    return new AccountDto(accountRepository.save(newAccount));
  }

  public AccountDto findUserAccount(Long userId) throws ResourceNotFoundException {
    Account account =
        accountRepository
            .findByUserId(userId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No account was found for this user. userId:"
                            .concat(String.valueOf(userId))));
    return new AccountDto(account);
  }

  public AccountDto findUserAccountByUserName(String userName) throws ResourceNotFoundException {
    User user = userService.findByUserName(userName);
    Account account =
        accountRepository
            .findByUserId(user.getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No account was found for this user. User with name:"
                            .concat(String.valueOf(userName))));
    return new AccountDto(account);
  }

  public AccountDto deposit(Long id, double sum)
      throws ResourceNotFoundException, InvalidDepositException {
    Account account =
        accountRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No account was found with the account id given for deposit operation. accountId:"
                            .concat(String.valueOf(id))));
    account.deposit(sum);
    account = accountRepository.save(account);
    return new AccountDto(account);
  }

  public AccountDto transfer(Long originAccountId, Long destinationAccountId, double amount)
      throws ResourceNotFoundException, InvalidTransferException {

    Account originAccount =
        accountRepository
            .findById(originAccountId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No account was found with the account id given to transfer from (origin account). accountId:"
                            .concat(String.valueOf(originAccountId))));
    Account destinationAccount =
        accountRepository
            .findById(destinationAccountId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No account was found with the account id given to transfer to (destination account). accountId:"
                            .concat(String.valueOf(destinationAccountId))));

    originAccount.transfer(amount);
    destinationAccount.receive(amount);
    accountRepository.save(originAccount);
    accountRepository.save(destinationAccount);

    return new AccountDto(originAccount);
  }
}
