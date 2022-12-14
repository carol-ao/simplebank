package com.carol.simplebank.service.account;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.model.Account;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.AccountRepository;
import com.carol.simplebank.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AccountServiceImpl implements AccountService{

  @Autowired private AccountRepository accountRepository;
  @Autowired private UserService userService;

  //ADMIN OPERATIONS

  @Override
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


  @Override
  public AccountDto findAccountByUserName(String userName) throws ResourceNotFoundException {
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

  @Override
  public AccountDto findById(Long id) throws ResourceNotFoundException {
    Account account =
            accountRepository
                    .findById(id)
                    .orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            "No account was found. Id:"
                                                    .concat(String.valueOf(id))));
    return new AccountDto(account);
  }


  @Override
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


  //BANK ACCOUNT OWNER OPERATIONS

  @Override
  public AccountDto consultAccount() throws ResourceNotFoundException {
    User user = getAccountOwner();
    Account account =
        accountRepository
            .findByUserId(user.getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No account was found for this user. userId:"
                            .concat(String.valueOf(user.getId()))));
    return new AccountDto(account);
  }

  @Override
  public AccountDto transfer(Long destinationAccountId, double amount)
      throws ResourceNotFoundException, InvalidTransferException {

    User user = getAccountOwner();

    Account originAccount =
        accountRepository
            .findByUserId(user.getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "This user does not have an account to transfer money from. userId:"
                            .concat(String.valueOf(user.getId()))));
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


  private User getAccountOwner() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (User) authentication.getPrincipal();
  }

}
