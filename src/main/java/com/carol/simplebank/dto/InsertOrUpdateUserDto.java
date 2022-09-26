package com.carol.simplebank.dto;

import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsertOrUpdateUserDto {

  private Long id;

  private String name;

  @CPF
  private String cpf;

  private String password;

  private Set<RoleDto> roles = new HashSet<RoleDto>();

}
