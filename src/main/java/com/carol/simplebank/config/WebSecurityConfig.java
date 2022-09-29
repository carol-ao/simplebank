package com.carol.simplebank.config;

import com.carol.simplebank.service.token.TokenService;
import com.carol.simplebank.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.carol.simplebank.util.Constants.ROLE_ADMIN;
import static com.carol.simplebank.util.Constants.ROLE_OPERATOR;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private UserService userService;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired private TokenService tokenService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/users").hasRole(ROLE_ADMIN)
        .antMatchers("/accounts").hasRole(ROLE_ADMIN)
        .antMatchers("/accounts/account-owner").hasAnyRole(ROLE_ADMIN,ROLE_OPERATOR)
        .antMatchers(HttpMethod.POST, "/auth").permitAll()
        .anyRequest().authenticated()
        .and().csrf()
        .disable().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().addFilterBefore(
                new TokenAuthenticationFilter(tokenService, userService), UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {}

  @Override
  @Bean
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }
}
