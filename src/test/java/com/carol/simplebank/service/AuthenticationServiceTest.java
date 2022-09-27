package com.carol.simplebank.service;

import com.carol.simplebank.dto.AuthenticationDto;
import com.carol.simplebank.dto.LoginForm;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.carol.simplebank.util.Constants.AUTHORIZATION_TYPE;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {

  @Mock private AuthenticationManager authenticationManager;

  @Mock private TokenService tokenService;

  @InjectMocks private AuthenticationService authenticationService;

  @Test
  public void mustAuthenticateUserWhenValidUserCredentialsGiven() {

    LoginForm loginForm = getValidUserLoginForm();
    User userWithEncryptedPassword = getUserWithEncryptedPassword();
    Authentication authentication = getAuthenticatedUser(userWithEncryptedPassword);
    String token = getValidToken();

    Mockito.when(
            authenticationManager.authenticate(
                Mockito.any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    Mockito.when(tokenService.generateToken(Mockito.any(Authentication.class), Mockito.anyLong()))
        .thenReturn(token);

    AuthenticationDto authenticationDto = authenticationService.authenticateUser(loginForm);

    Assertions.assertEquals(userWithEncryptedPassword.getId(), authenticationDto.getUserId());
    Assertions.assertEquals(token, authenticationDto.getToken());
    Assertions.assertEquals(AUTHORIZATION_TYPE, authenticationDto.getAuthorizationType());
  }

  @Test
  public void mustReturnNullWhenAuthenticationFails() {

    LoginForm loginForm = getValidUserLoginForm();

    Mockito.when(
            authenticationManager.authenticate(
                Mockito.any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(RuntimeException.class);

    AuthenticationDto authenticationDto = authenticationService.authenticateUser(loginForm);

    Assertions.assertNull(authenticationDto);
  }

  private LoginForm getValidUserLoginForm() {

    return LoginForm.builder().password("123").username("John Snow").build();
  }

  private UsernamePasswordAuthenticationToken getAuthenticatedUser(User userWithEncryptedPassword) {

    List<SimpleGrantedAuthority> simpleGrantedAuthorities =
        Arrays.asList(
            new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_ADMIN"));
    return new UsernamePasswordAuthenticationToken(
        userWithEncryptedPassword, null, new HashSet<>(simpleGrantedAuthorities));
  }

  private String getValidToken() {
    return "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTaW1wbGViYW5rIEFQSSIsInN1YiI6IjEiLCJpYXQiOjE2NjQzMDIzODQsImV4cCI6MTY2NDM4ODc4NH0.h9cjH5LMozfXTHBd3VAzYja3Eacqd65HHkmNeGlQrtk";
  }

  private User getUserWithEncryptedPassword() {
    return User.builder()
        .id(1L)
        .password("encryptedPassord")
        .cpf("052.468.324-73")
        .name("Milly Alcock")
        .roles(
            new HashSet<Role>(
                Arrays.asList(
                    Role.builder().id(1L).authority("ROLE_ADMIN").build(),
                    Role.builder().id(2L).authority("ROLE_OPERATOR").build())))
        .build();
  }
}
