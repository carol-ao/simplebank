package com.carol.simplebank.service.authentication;

import com.carol.simplebank.dto.LoginForm;
import com.carol.simplebank.dto.AuthenticationDto;
import com.carol.simplebank.model.User;
import com.carol.simplebank.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.carol.simplebank.util.Constants.AUTHORIZATION_TYPE;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired private AuthenticationManager authenticationManager;

    @Autowired private TokenService tokenService;

    @Override
    public AuthenticationDto authenticateUser(LoginForm loginForm) {

        UsernamePasswordAuthenticationToken loginData =
                loginForm.toUsernamePasswordAuthenticationToken();
        try {
            Authentication authentication = authenticationManager.authenticate(loginData);
            User user = getUserFromAuthentication(authentication);
            String token = tokenService.generateToken(authentication, user.getId());
            return new AuthenticationDto(user.getId(), token, AUTHORIZATION_TYPE);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
