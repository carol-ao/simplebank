package com.carol.simplebank.controller;

import com.carol.simplebank.dto.LoginForm;
import com.carol.simplebank.dto.TokenDto;
import com.carol.simplebank.service.TokenService;
import com.carol.simplebank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

  public static final String TOKEN_TYPE = "Bearer";

  @Autowired private TokenService tokenService;

  @Autowired private AuthenticationManager authenticationManager;

  @PostMapping
  public ResponseEntity<TokenDto> authenticateUser(@Valid @RequestBody LoginForm loginForm) {
    //TODO: refactor into AuthenticationService
    UsernamePasswordAuthenticationToken loginData =
        loginForm.toUsernamePasswordAuthenticationToken();

    try {
      Authentication authentication = authenticationManager.authenticate(loginData);
      String token = tokenService.generateToken(authentication);
      return ResponseEntity.ok(new TokenDto(token, TOKEN_TYPE));
    } catch (AuthenticationException e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
