package com.carol.simplebank.service;

import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.repositories.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class RoleServiceTest {

  @Mock private RoleRepository roleRepository;

  @InjectMocks private RoleService roleService;

  @Test
  public void mustReturnRoleWhenValidRoleIdGivenToSearchRole() throws ResourceNotFoundException {

    Role role = getValidRole();

    Mockito.when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

    Role roleFound = roleService.findById(role.getId());

    Assertions.assertEquals(role, roleFound);
  }

  private Role getValidRole() {
    return Role.builder().id(1L).authority("ROLE_ADMIN").build();
  }
}
