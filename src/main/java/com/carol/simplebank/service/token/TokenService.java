package com.carol.simplebank.service.token;

import org.springframework.security.core.Authentication;

public interface TokenService {

  boolean isTokenValid(String token);

  Long getUserIdFromToken(String token);

  String generateToken(Authentication authentication, Long userId);
}
