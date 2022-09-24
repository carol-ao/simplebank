package com.carol.simplebank.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
@NoArgsConstructor
public class LoginForm {

    private String username;
    private String password;

    public UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken(){
        return new UsernamePasswordAuthenticationToken(username,password);
    }
}
