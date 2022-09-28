package com.carol.simplebank.controller;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.InvalidDepositException;
import com.carol.simplebank.exceptions.InvalidTransferException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/accounts")
public class AccountController {

  @Autowired private AccountService accountService;

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

  @GetMapping(params = "{id}")
  public ResponseEntity<AccountDto> findAccountById(@RequestParam("id") Long id)
      throws ResourceNotFoundException {
    return ResponseEntity.ok().body(accountService.findById(id));
  }

  @GetMapping(path = "/user-operations/consult")
  public ResponseEntity<AccountDto> ConsultAccount() throws ResourceNotFoundException {
    return ResponseEntity.ok().body(accountService.consultAccount());
  }

  @PatchMapping("/user-operations/deposit")
  public ResponseEntity<AccountDto> depositInAccount(
      @RequestParam(required = true, name = "accountId") Long accountId,
      @RequestParam(required = true, name = "depositAmount") double amount)
      throws InvalidDepositException, ResourceNotFoundException {
    return ResponseEntity.ok().body(accountService.deposit(accountId, amount));
  }

  @PatchMapping("/user-operations/transfer")
  public ResponseEntity<AccountDto> transfer(
      @RequestParam(required = true, name = "originAccountId") Long originAccountId,
      @RequestParam(required = true, name = "destinationAccountId") Long destinationAccountId,
      @RequestParam(required = true, name = "transferAmount") double amount)
      throws ResourceNotFoundException, InvalidTransferException {
    return ResponseEntity.ok()
        .body(accountService.transfer(originAccountId, destinationAccountId, amount));
  }
}
