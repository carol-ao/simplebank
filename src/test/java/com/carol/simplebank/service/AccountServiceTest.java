package com.carol.simplebank.service;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.model.Account;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class AccountServiceTest {

  @InjectMocks private AccountService accountService;

  @Mock private AccountRepository accountRepository;

  @Mock private UserService userService;

  @Test
  public void mustOpenAccountWhenUserValidAndUserHasNoAccount()
      throws ResourceNotFoundException, DuplicateAccountException {

    User user = getUser1();

    Mockito.when(userService.findEntityById(user.getId())).thenReturn(user);
    Mockito.when(accountRepository.existsByUserId(user.getId())).thenReturn(false);
    Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenReturn(getEmptyAccount());

    AccountDto accountDto = accountService.openAccount(user.getId());

    Assertions.assertNotNull(accountDto.getId());
    Assertions.assertEquals(0.0, accountDto.getBalance());
    Assertions.assertEquals(user.getId(), accountDto.getUserId());
  }

  @Test
  public void mustThrowDuplicateAccountExceptionWhenUserValidAndUserAlreadyHasAnAccount()
      throws ResourceNotFoundException, DuplicateAccountException {

    User user = getUser1();

    Mockito.when(userService.findEntityById(user.getId())).thenReturn(user);
    Mockito.when(accountRepository.existsByUserId(user.getId())).thenReturn(true);

    Exception exception =
        Assertions.assertThrows(
            DuplicateAccountException.class, () -> accountService.openAccount(user.getId()));

    Assertions.assertEquals(
        "User already has an account and cannot open a second one. userId:"
            .concat(String.valueOf(user.getId())),
        exception.getMessage());
  }

  @Test
  public void mustReturnAccountDtoWhenAccountExistsForUserIdGiven()
      throws ResourceNotFoundException {

    User user = getUser1();
    Account account = getAccountWithSmallValueBalance(user);
    AccountDto expectedAccountDto = new AccountDto(account);

    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

    AccountDto accountDto = accountService.findUserAccount(user.getId());

    Assertions.assertEquals(expectedAccountDto.getId(), accountDto.getId());
    Assertions.assertEquals(expectedAccountDto.getUserId(), accountDto.getUserId());
    Assertions.assertEquals(expectedAccountDto.getBalance(), accountDto.getBalance());
  }

  @Test
  public void mustThrowResourceNotFoundExceptionWhenAccountNotFoundInFindUserAccountMethod() {

    Long userId = 1L;

    Mockito.when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class, () -> accountService.findUserAccount(userId));

    Assertions.assertEquals(
        "No account was found for this user. userId:".concat(String.valueOf(userId)),
        exception.getMessage());
  }

  @Test
  public void mustReturnAccountDtoWhenAccountExistsForUserNameGiven()
      throws ResourceNotFoundException {

    User user = getUser1();
    Account account = getAccountWithSmallValueBalance(user);
    AccountDto expectedAccountDto = new AccountDto(account);

    Mockito.when(userService.findByUserName(user.getName())).thenReturn(user);
    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

    AccountDto accountDto = accountService.findUserAccountByUserName(user.getName());

    Assertions.assertEquals(expectedAccountDto.getId(), accountDto.getId());
    Assertions.assertEquals(expectedAccountDto.getUserId(), accountDto.getUserId());
    Assertions.assertEquals(expectedAccountDto.getBalance(), accountDto.getBalance());
  }

  @Test
  public void
      mustThrowResourceNotFoundExceptionWhenAccountNotFoundInFindUserAccounByUserNameMethod()
          throws ResourceNotFoundException {

    User user = getUser1();

    Mockito.when(userService.findByUserName(user.getName())).thenReturn(user);
    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class,
            () -> accountService.findUserAccountByUserName(user.getName()));

    Assertions.assertEquals(
        "No account was found for this user. User with name:"
            .concat(String.valueOf(user.getName())),
        exception.getMessage());
  }

  @Test
  public void mustMakeDepositWhenAccountExistsInDepositAttempt()
      throws InvalidDepositException, ResourceNotFoundException {

    Account account = getAccountWithBigValueBalance(getUser1());
    double depositedAmount = 100.00;

    Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
    account.deposit(depositedAmount);
    Mockito.when(accountRepository.save(account)).thenReturn(account);

    AccountDto accountDto = accountService.deposit(account.getId(), depositedAmount);

    Assertions.assertEquals(accountDto.getId(), account.getId());
    Assertions.assertEquals(accountDto.getBalance(), account.getBalance());
    Assertions.assertEquals(accountDto.getUserId(), account.getUser().getId());
  }

  @Test
  public void mustThrowResourceNotFoundExceptionWhenAccountDoesntExistInDepositAttempt() {

    Account account = getAccountWithBigValueBalance(getUser1());
    double depositedAmount = 100.00;

    Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class,
            () -> accountService.deposit(account.getId(), depositedAmount));
    Assertions.assertEquals(
        "No account was found with the account id given for deposit operation. accountId:"
            .concat(account.getId().toString()),
        exception.getMessage());
  }

  @Test
  public void mustTrasferMoneyWhenBothAccountsExistsInTransferAttempt()
      throws InvalidTransferException, ResourceNotFoundException {

    Account originAccount = getAccountWithBigValueBalance(getUser1());
    Account destinationAccount = getAccountWithSmallValueBalance(getUser2());
    double transferredAmount = 100.00;

    Mockito.when(accountRepository.findById(originAccount.getId()))
        .thenReturn(Optional.of(originAccount));
    Mockito.when(accountRepository.findById(destinationAccount.getId()))
        .thenReturn(Optional.of(destinationAccount));
    originAccount.transfer(transferredAmount);
    destinationAccount.receive(transferredAmount);

    Mockito.when(accountRepository.save(originAccount)).thenReturn(originAccount);
    Mockito.when(accountRepository.save(destinationAccount)).thenReturn(destinationAccount);

    AccountDto accountDto =
        accountService.transfer(
            originAccount.getId(), destinationAccount.getId(), transferredAmount);

    Assertions.assertEquals(accountDto.getId(), originAccount.getId());
    Assertions.assertEquals(accountDto.getBalance(), originAccount.getBalance());
    Assertions.assertEquals(accountDto.getUserId(), originAccount.getUser().getId());
  }

  @Test
  public void mustThrowResourceNotFoundExceptionWhenOriginAccountDoesntExistInTransferAttempt() {

    Account originAccount = getAccountWithBigValueBalance(getUser1());
    Account destinationAccount = getAccountWithSmallValueBalance(getUser2());
    double transferredAmount = 100.00;

    Mockito.when(accountRepository.findById(originAccount.getId())).thenReturn(Optional.empty());
    Mockito.when(accountRepository.findById(destinationAccount.getId()))
        .thenReturn(Optional.of(destinationAccount));

    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class,
            () ->
                accountService.transfer(
                    originAccount.getId(), destinationAccount.getId(), transferredAmount));
    Assertions.assertEquals(
        "No account was found with the account id given to transfer from (origin account). accountId:"
            .concat(String.valueOf(originAccount.getId())),
        exception.getMessage());
  }

  @Test
  public void
      mustThrowResourceNotFoundExceptionWhenDestinationAccountDoesntExistInTransferAttempt() {

    Account originAccount = getAccountWithBigValueBalance(getUser1());
    Account destinationAccount = getAccountWithSmallValueBalance(getUser2());
    double transferredAmount = 100.00;

    Mockito.when(accountRepository.findById(originAccount.getId()))
        .thenReturn(Optional.of(originAccount));
    Mockito.when(accountRepository.findById(destinationAccount.getId()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class,
            () ->
                accountService.transfer(
                    originAccount.getId(), destinationAccount.getId(), transferredAmount));
    Assertions.assertEquals(
        "No account was found with the account id given to transfer to (destination account). accountId:"
            .concat(String.valueOf(destinationAccount.getId())),
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
