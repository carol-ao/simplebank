package com.carol.simplebank.config;

import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.model.User;
import com.carol.simplebank.service.TokenService;
import com.carol.simplebank.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.carol.simplebank.util.Constants.AUTHORIZATION_TYPE;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private TokenService tokenService;
  private UserService userService;

  public TokenAuthenticationFilter(TokenService tokenService, UserService userService) {
    this.tokenService = tokenService;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = retrieveToken(request);
    if (tokenService.isTokenValid(token)) {
      try {
        authenticateUser(token);
      } catch (Exception e) {
        // TODO: handle this scenario
      }
    }
    filterChain.doFilter(request, response);
  }

  private void authenticateUser(String token) throws ResourceNotFoundException {
    Long userId = tokenService.getUserIdFromToken(token);
    User user = userService.findEntityById(userId);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private String retrieveToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader == null
        || authorizationHeader.isEmpty()
        || !authorizationHeader.startsWith(AUTHORIZATION_TYPE.concat(" "))) {
      return null;
    }
    String token = authorizationHeader.replace(AUTHORIZATION_TYPE.concat(" "), "");
    return token;
  }
}
