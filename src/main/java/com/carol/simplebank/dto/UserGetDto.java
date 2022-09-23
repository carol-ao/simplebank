package com.carol.simplebank.dto;

import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGetDto {

  private String name;

  private String cpf;

  private String password;

  private Set<RoleDto> roleDtos = new HashSet<RoleDto>();

  public static UserGetDto toDto(User user) {
    return UserGetDto.builder()
        .name(user.getName())
        .cpf(user.getCpf())
        .roleDtos(Role.toDtos(user.getRoles()))
        .build();
  }
}
