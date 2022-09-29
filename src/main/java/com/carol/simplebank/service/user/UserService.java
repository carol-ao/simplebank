package com.carol.simplebank.service.user;

import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.UserDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.transaction.Transactional;
import java.util.List;

public interface UserService extends UserDetailsService {

  @Transactional
  UserDto save(InsertOrUpdateUserDto insertOrUpdateUserDto)
      throws ResourceNotFoundException, DuplicateUserException, UserWithNoRolesException;

  @Transactional
  UserDto patch(InsertOrUpdateUserDto insertOrUpdateUserDto) throws ResourceNotFoundException;

  @Transactional
  void delete(Long id) throws ResourceNotFoundException;

  Page<UserDto> findAll(Pageable pageable);

  UserDto findById(Long id) throws ResourceNotFoundException;

  UserDto findByCpf(String cpf) throws ResourceNotFoundException;

  User findByUserName(String userName) throws ResourceNotFoundException;

  User findEntityById(Long id) throws ResourceNotFoundException;
}
