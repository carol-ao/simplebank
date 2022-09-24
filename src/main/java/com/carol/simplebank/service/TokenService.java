package com.carol.simplebank.service;

import com.carol.simplebank.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

  @Value("${jwt.duration}")
  private Integer jwtDuration;

  @Value("${jwt.secret}")
  private String jwtSecret;

  public String generateToken(Authentication authentication) {

    Date now = new Date();
    User user = (User) authentication.getPrincipal();
    return Jwts.builder()
        .setIssuer("Simplebank API")
        .setSubject(user.getId().toString())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + jwtDuration))
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }


  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(token).getBody();
    return Long.parseLong(claims.getSubject());
  }
}
