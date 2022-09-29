package com.carol.simplebank.service;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.factory.AccountFactory;
import com.carol.simplebank.factory.UserFactory;
import com.carol.simplebank.model.Account;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.AccountRepository;
import com.carol.simplebank.service.account.AccountServiceImpl;
import com.carol.simplebank.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class AccountServiceImplTest {

  @InjectMocks private AccountServiceImpl accountService;

  @Mock private AccountRepository accountRepository;

  @Mock private UserService userService;

  @Mock private Authentication authentication;

  @Mock private SecurityContext securityContext;

  @BeforeEach
  public void setUp() {
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void mustOpenAccountWhenUserValidAndUserHasNoAccount()
      throws ResourceNotFoundException, DuplicateAccountException {

    User user = UserFactory.getUser1();

    Mockito.when(userService.findEntityById(user.getId())).thenReturn(user);
    Mockito.when(accountRepository.existsByUserId(user.getId())).thenReturn(false);
    Mockito.when(accountRepository.save(Mockito.any(Account.class)))
        .thenReturn(AccountFactory.getEmptyAccountForUser1());

    AccountDto accountDto = accountService.openAccount(user.getId());

    Assertions.assertNotNull(accountDto.getId());
    Assertions.assertEquals(0.0, accountDto.getBalance());
    Assertions.assertEquals(user.getId(), accountDto.getUserId());
  }

  @Test
  public void mustThrowDuplicateAccountExceptionWhenUserValidAndUserAlreadyHasAnAccount()
      throws ResourceNotFoundException {

    User user = UserFactory.getUser1();

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
  public void mustReturnAccountDtoWhenAccountSearchedByAuthenticatedOwnerWithValidAccount()
      throws ResourceNotFoundException {

    User user = UserFactory.getUser1();
    Account account = AccountFactory.getAccountWithBigValueBalanceForUser1();
    AccountDto expectedAccountDto = new AccountDto(account);

    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));
    Mockito.when(authentication.getPrincipal()).thenReturn(UserFactory.getUser1());

    AccountDto accountDto = accountService.consultAccount();

    Assertions.assertEquals(expectedAccountDto.getId(), accountDto.getId());
    Assertions.assertEquals(expectedAccountDto.getUserId(), accountDto.getUserId());
    Assertions.assertEquals(expectedAccountDto.getBalance(), accountDto.getBalance());
  }

  @Test
  public void
      mustThrowResourceNotFoundExceptionWhenAuthenticatedOwnerTriesToConsultAccountButHasNone() {

    Long userId = 1L;

    Mockito.when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());
    Mockito.when(authentication.getPrincipal()).thenReturn(UserFactory.getUser1());
    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class, () -> accountService.consultAccount());

    Assertions.assertEquals(
        "No account was found for this user. userId:".concat(String.valueOf(userId)),
        exception.getMessage());
  }

  @Test
  public void mustReturnAccountDtoWhenAccountSearchedByIdAndAccountIsFound()
      throws ResourceNotFoundException {

    Account account = AccountFactory.getAccountWithBigValueBalanceForUser1();
    AccountDto expectedAccountDto = new AccountDto(account);

    Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

    AccountDto accountDto = accountService.findById(account.getId());

    Assertions.assertEquals(expectedAccountDto.getId(), accountDto.getId());
    Assertions.assertEquals(expectedAccountDto.getUserId(), accountDto.getUserId());
    Assertions.assertEquals(expectedAccountDto.getBalance(), accountDto.getBalance());
  }

  @Test
  public void
      mustThrowResourceNotFoundExceptionWhenAuthenticatedUserTriesToConsultButUserNotFoundInDatabase() {

    Long userId = 1L;

    Mockito.when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());
    Mockito.when(authentication.getPrincipal()).thenReturn(UserFactory.getUser1());

    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class, () -> accountService.consultAccount());

    Assertions.assertEquals(
        "No account was found for this user. userId:".concat(String.valueOf(userId)),
        exception.getMessage());
  }

  @Test
  public void mustReturnAccountDtoWhenAccountSearchedByNameOfUserAndAccountIsFound()
      throws ResourceNotFoundException {

    User user = UserFactory.getUser1();
    Account account = AccountFactory.getAccountWithBigValueBalanceForUser1();
    AccountDto expectedAccountDto = new AccountDto(account);

    Mockito.when(userService.findByUserName(user.getName())).thenReturn(user);
    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

    AccountDto accountDto = accountService.findAccountByUserName(user.getName());

    Assertions.assertEquals(expectedAccountDto.getId(), accountDto.getId());
    Assertions.assertEquals(expectedAccountDto.getUserId(), accountDto.getUserId());
    Assertions.assertEquals(expectedAccountDto.getBalance(), accountDto.getBalance());
  }

  @Test
  public void
      mustThrowResourceNotFoundExceptionWhenAccountNotFoundInFindUserAccountByUserNameMethod()
          throws ResourceNotFoundException {

    User user = UserFactory.getUser1();

    Mockito.when(userService.findByUserName(user.getName())).thenReturn(user);
    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class,
            () -> accountService.findAccountByUserName(user.getName()));

    Assertions.assertEquals(
        "No account was found for this user. User with name:"
            .concat(String.valueOf(user.getName())),
        exception.getMessage());
  }

  @Test
  public void mustMakeDepositWhenAccountExistsInDepositAttempt()
      throws InvalidDepositException, ResourceNotFoundException {

    Account account = AccountFactory.getAccountWithBigValueBalanceForUser1();
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

    double depositedAmount = 100.00;

    Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class, () -> accountService.deposit(1L, depositedAmount));
    Assertions.assertEquals(
        "No account was found with the account id given for deposit operation. accountId:" + 1L,
        exception.getMessage());
  }

  @Test
  public void mustTransferMoneyWhenBothAccountsExistsInTransferAttempt()
      throws InvalidTransferException, ResourceNotFoundException {

    Account originAccount = AccountFactory.getAccountWithBigValueBalanceForUser1();
    User ownerOfOriginAccount = originAccount.getUser();
    Account destinationAccount = AccountFactory.getAccountWithSmallValueBalanceForUser2();
    double transferredAmount = 100.00;

    Mockito.when(accountRepository.findByUserId(ownerOfOriginAccount.getId()))
        .thenReturn(Optional.of(originAccount));
    Mockito.when(accountRepository.findById(destinationAccount.getId()))
        .thenReturn(Optional.of(destinationAccount));
    originAccount.transfer(transferredAmount);
    destinationAccount.receive(transferredAmount);

    Mockito.when(authentication.getPrincipal()).thenReturn(ownerOfOriginAccount);
    Mockito.when(accountRepository.save(originAccount)).thenReturn(originAccount);
    Mockito.when(accountRepository.save(destinationAccount)).thenReturn(destinationAccount);

    AccountDto accountDto = accountService.transfer(destinationAccount.getId(), transferredAmount);

    Assertions.assertEquals(accountDto.getId(), originAccount.getId());
    Assertions.assertEquals(accountDto.getBalance(), originAccount.getBalance());
    Assertions.assertEquals(accountDto.getUserId(), ownerOfOriginAccount.getId());
  }

  @Test
  public void mustThrowResourceNotFoundExceptionWhenOriginAccountDoesntExistInTransferAttempt() {

    User userWithNoAccount = UserFactory.getUser1();

    Account destinationAccount = AccountFactory.getAccountWithSmallValueBalanceForUser2();
    double transferredAmount = 100.00;

    Mockito.when(authentication.getPrincipal()).thenReturn(userWithNoAccount);
    Mockito.when(accountRepository.findByUserId(userWithNoAccount.getId()))
        .thenReturn(Optional.empty());
    Mockito.when(accountRepository.findById(destinationAccount.getId()))
        .thenReturn(Optional.of(destinationAccount));

    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class,
            () -> accountService.transfer(destinationAccount.getId(), transferredAmount));
    Assertions.assertEquals(
        "This user does not have an account to transfer money from. userId:"
            .concat(String.valueOf(userWithNoAccount.getId())),
        exception.getMessage());
  }

  @Test
  public void
      mustThrowResourceNotFoundExceptionWhenDestinationAccountDoesntExistInTransferAttempt() {

    Account originAccount = AccountFactory.getAccountWithBigValueBalanceForUser1();
    User originUser = originAccount.getUser();
    Account destinationAccount = AccountFactory.getAccountWithSmallValueBalanceForUser2();
    double transferredAmount = 100.00;

    Mockito.when(authentication.getPrincipal()).thenReturn(originUser);
    Mockito.when(accountRepository.findByUserId(originUser.getId()))
        .thenReturn(Optional.of(originAccount));
    Mockito.when(accountRepository.findById(destinationAccount.getId()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            ResourceNotFoundException.class,
            () -> accountService.transfer(destinationAccount.getId(), transferredAmount));
    Assertions.assertEquals(
        "No account was found with the account id given to transfer to (destination account). accountId:"
            .concat(String.valueOf(destinationAccount.getId())),
        exception.getMessage());
  }
}
