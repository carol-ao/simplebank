package com.carol.simplebank.repositories;

import com.carol.simplebank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    public Optional<User> findByCpf(String cpf);

    public Optional<User> findByName(String userName);
}
