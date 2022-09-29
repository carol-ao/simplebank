package com.carol.simplebank.controller.account;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/accounts")
public class AccountController {

  @Autowired private AccountService accountService;

  //ADMIN OPERATIONS

  @PostMapping
  public ResponseEntity<AccountDto> openAccount(@RequestParam("userId") Long userId)
      throws ResourceNotFoundException, DuplicateAccountException {
    // TODO: change to return uri too
    return ResponseEntity.ok().body(accountService.openAccount(userId));
  }

  @GetMapping(params = "userName")
  public ResponseEntity<AccountDto> findUserAccountByUserName(
      @RequestParam("userName") String userName) throws ResourceNotFoundException {
    return ResponseEntity.ok().body(accountService.findAccountByUserName(userName));
  }

  @GetMapping(params = "/{id}")
  public ResponseEntity<AccountDto> findAccountById(@PathVariable("id") Long id)
      throws ResourceNotFoundException {
    return ResponseEntity.ok().body(accountService.findById(id));
  }

  @PatchMapping("/deposit")
  public ResponseEntity<AccountDto> depositInAccount(
          @RequestParam(required = true, name = "accountId") Long accountId,
          @RequestParam(required = true, name = "depositAmount") double amount)
          throws InvalidDepositException, ResourceNotFoundException {
    return ResponseEntity.ok().body(accountService.deposit(accountId, amount));
  }

  //BANK ACCOUNT OWNER OPERATIONS

  @GetMapping(path = "/account-owner/consult")
  public ResponseEntity<AccountDto> ConsultAccount() throws ResourceNotFoundException {
    return ResponseEntity.ok().body(accountService.consultAccount());
  }

  @PatchMapping("/account-owner/transfer")
  public ResponseEntity<AccountDto> transfer(
      @RequestParam(required = true, name = "destinationAccountId") Long destinationAccountId,
      @RequestParam(required = true, name = "transferAmount") double amount)
      throws ResourceNotFoundException, InvalidTransferException {
    return ResponseEntity.ok()
        .body(accountService.transfer(destinationAccountId, amount));
  }
}
