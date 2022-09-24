package com.carol.simplebank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Autowired private JwtTokenStore jwtTokenStore;

  private static final String[] PUBLIC = {"/oauth/token"};

  private static final String[] OPERATOR_OR_ADMIN = {"/users/**"};

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.tokenStore(jwtTokenStore);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .antMatchers(PUBLIC)
        .permitAll()
        .antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN)
        .permitAll()
        .antMatchers(HttpMethod.POST,OPERATOR_OR_ADMIN).hasRole("ADMIN")
        .anyRequest()
        .authenticated();
  }
}
