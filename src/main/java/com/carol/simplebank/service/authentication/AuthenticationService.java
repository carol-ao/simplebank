package com.carol.simplebank.service.authentication;

import com.carol.simplebank.dto.AuthenticationDto;
import com.carol.simplebank.dto.LoginForm;

public interface AuthenticationService {

  AuthenticationDto authenticateUser(LoginForm loginForm);
}
