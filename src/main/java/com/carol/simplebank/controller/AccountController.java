package com.carol.simplebank.controller;

import com.carol.simplebank.dto.AccountDto;
import com.carol.simplebank.exceptions.DuplicateAccountException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/accounts")
public class AccountController {

  @Autowired private AccountService accountService;

  @PostMapping
  public ResponseEntity<AccountDto> openAccount(@RequestParam("userId") Long userId)
      throws ResourceNotFoundException, DuplicateAccountException {
    // TODO: change to return uri
    return ResponseEntity.ok().body(accountService.openAccount(userId));
  }
}
