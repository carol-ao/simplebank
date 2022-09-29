package com.carol.simplebank.service.role;

import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.model.Role;

public interface RoleService {

  Role findById(Long id) throws ResourceNotFoundException;
}
