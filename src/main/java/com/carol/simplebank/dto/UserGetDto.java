package com.carol.simplebank.dto;

import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGetDto {

  private Long id;

  private String name;

  private String cpf;

  private String password;

  private Set<RoleDto> roleDtos = new HashSet<RoleDto>();

  public static UserGetDto toDto(User user) {
    return UserGetDto.builder()
        .id(user.getId())
        .name(user.getName())
        .cpf(user.getCpf())
        .roleDtos(Role.toDtos(user.getRoles()))
        .build();
  }

  public static List<UserGetDto> toDtos(List<User> users) {
    return users.stream()
        .map(
            user ->
                UserGetDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .cpf(user.getCpf())
                    .roleDtos(Role.toDtos(user.getRoles()))
                    .build())
        .collect(Collectors.toList());
  }
}
