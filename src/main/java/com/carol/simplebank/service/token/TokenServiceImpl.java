package com.carol.simplebank.service.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService{

  @Value("${jwt.duration}")
  private Integer jwtDuration;

  @Value("${jwt.secret}")
  private String jwtSecret;

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

  public String generateToken(Authentication authentication, Long userId) {
    Date now = new Date();
    return Jwts.builder()
        .setIssuer("Simplebank API")
        .setSubject(userId.toString())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + jwtDuration))
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
  }
}
