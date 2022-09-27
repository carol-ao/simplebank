package com.carol.simplebank.controller;

import com.carol.simplebank.dto.AuthenticationDto;
import com.carol.simplebank.dto.LoginForm;
import com.carol.simplebank.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.carol.simplebank.util.Constants.AUTHORIZATION_TYPE;

@ExtendWith(SpringExtension.class)
public class AuthenticationControllerTest {

  @Mock private AuthenticationService authenticationService;

  @InjectMocks private AuthenticationController authenticationController;

  @Test
  public void
      mustReturnStatusOkAndAuthenticationDtoWithTokenWhenValidUserCredentialsGivenInAutheticationAttempt() {

    LoginForm loginForm = getValidUserLoginForm();
    AuthenticationDto authenticationDto =
        new AuthenticationDto(1L, "some_token", AUTHORIZATION_TYPE);

    Mockito.when(authenticationService.authenticateUser(Mockito.any()))
        .thenReturn(authenticationDto);

    ResponseEntity<AuthenticationDto> response =
        authenticationController.authenticateUser(loginForm);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(1L, response.getBody().getUserId());
    Assertions.assertEquals("some_token", response.getBody().getToken());
    Assertions.assertEquals(AUTHORIZATION_TYPE, response.getBody().getAuthorizationType());
    //      return ResponseEntity.badRequest().body(null);
  }

  @Test
  public void mustReturnBadRequestAndNullInBodyWhenUserWithBadCredentialsAttemptsAuthentication() {

    LoginForm invalidCredentialsLoginForm = new LoginForm("some_user", "wrong_password");

    Mockito.when(authenticationService.authenticateUser(Mockito.any())).thenReturn(null);

    ResponseEntity<AuthenticationDto> response =
        authenticationController.authenticateUser(invalidCredentialsLoginForm);

    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals(null, response.getBody());
  }

  private LoginForm getValidUserLoginForm() {
    return LoginForm.builder().password("123").username("John Snow").build();
  }
}
