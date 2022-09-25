package com.carol.simplebank.repositories;


import com.carol.simplebank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
