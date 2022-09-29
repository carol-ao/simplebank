package com.carol.simplebank.controller.authentication;

import com.carol.simplebank.dto.LoginForm;
import com.carol.simplebank.dto.AuthenticationDto;
import com.carol.simplebank.service.authentication.AuthenticationService;
import com.carol.simplebank.service.authentication.AuthenticationServiceImpl;
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

  @Autowired private AuthenticationService authenticationServiceImpl;

  @PostMapping
  public ResponseEntity<AuthenticationDto> authenticateUser(@Valid @RequestBody LoginForm loginForm) {

    AuthenticationDto authenticationDto = authenticationServiceImpl.authenticateUser(loginForm);
    if (authenticationDto != null) {
      return ResponseEntity.ok(authenticationDto);
    } else {
      return ResponseEntity.badRequest().body(null);
    }
  }
}
