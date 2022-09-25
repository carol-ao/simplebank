package com.carol.simplebank.controller;

import com.carol.simplebank.dto.LoginForm;
import com.carol.simplebank.dto.AuthenticationDto;
import com.carol.simplebank.service.AuthenticationService;
import com.carol.simplebank.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

  @Autowired private AuthenticationService authenticationService;

  @PostMapping
  public ResponseEntity<AuthenticationDto> authenticateUser(@Valid @RequestBody LoginForm loginForm) {

    AuthenticationDto authenticationDto = authenticationService.authenticateUser(loginForm);
    if (authenticationDto != null) {
      return ResponseEntity.ok(authenticationDto);
    } else {
      return ResponseEntity.badRequest().body(null);
    }
  }
}
