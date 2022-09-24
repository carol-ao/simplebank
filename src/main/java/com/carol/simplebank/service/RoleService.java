package com.carol.simplebank.service;

import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findById(Long id) throws ResourceNotFoundException {
        return roleRepository.findById(id).orElseThrow(() ->  new ResourceNotFoundException("Role id "+ id +" not found."));
    }
}
