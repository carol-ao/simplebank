package com.carol.simplebank.dto;

import com.carol.simplebank.model.Role;
import lombok.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

  private Long id;
  private String authority;

  public static Set<Role> toRoles(Set<RoleDto> roleDtos) {
    return roleDtos.stream()
        .map(roleDto -> Role.builder().id(roleDto.id).authority(roleDto.authority).build())
        .collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RoleDto roleDto = (RoleDto) o;
    return Objects.equals(id, roleDto.id) && Objects.equals(authority, roleDto.authority);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, authority);
  }
}
