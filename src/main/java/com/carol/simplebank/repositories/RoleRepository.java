package com.carol.simplebank.repositories;

import com.carol.simplebank.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
