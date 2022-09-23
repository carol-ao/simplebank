package com.carol.simplebank.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPostDto {

    private String name;

    private String cpf;

    private String password;

    private Set<RoleDto> roles = new HashSet<RoleDto>();


}
