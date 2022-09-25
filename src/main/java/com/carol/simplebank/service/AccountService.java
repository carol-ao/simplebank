package com.carol.simplebank.service;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
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
                () -> new ResourceNotFoundException("No account for this user was found."));

    return new AccountDto(account);
  }

  public AccountDto openAccount(Long userId)
      throws ResourceNotFoundException, DuplicateAccountException {
    User user = userService.findById(userId);
    if (accountRepository.existsByUserId(userId)) {
      throw new DuplicateAccountException(
          "User already has an account and cannot open a second one.");
    }
    Account newAccount = Account.builder().user(user).build();
    return new AccountDto(accountRepository.save(newAccount));
  }
}
