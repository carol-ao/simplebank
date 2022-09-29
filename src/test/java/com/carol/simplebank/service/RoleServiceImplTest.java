package com.carol.simplebank.service;

import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.factory.RoleFactory;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.repositories.RoleRepository;
import com.carol.simplebank.service.role.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class RoleServiceImplTest {

  @Mock private RoleRepository roleRepository;

  @InjectMocks private RoleServiceImpl roleService;

  @Test
  public void mustReturnRoleWhenValidRoleIdGivenToSearchRole() throws ResourceNotFoundException {

    Role role = RoleFactory.getAdminRole();

    Mockito.when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

    Role roleFound = roleService.findById(role.getId());

    Assertions.assertEquals(role, roleFound);
  }
}
