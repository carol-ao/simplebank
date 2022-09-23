package com.carol.simplebank.model;

import com.carol.simplebank.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String authority;

  public static Set<RoleDto> toDtos(Set<Role> roles) {
    return roles.stream().map(role -> new RoleDto(role.id,role.authority)).collect(Collectors.toSet());
  }
}
