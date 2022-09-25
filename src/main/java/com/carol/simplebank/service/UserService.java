package com.carol.simplebank.service;

import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.RoleDto;
import com.carol.simplebank.dto.UserDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.IllegalOperationException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.model.Account;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.AccountRepository;
import com.carol.simplebank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// TODO: create interfaces!!!

@Service
public class UserService implements UserDetailsService {

  @Autowired private UserRepository userRepository;

  @Autowired private RoleService roleService;

  @Autowired private AccountRepository accountRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Transactional
  public UserDto save(InsertOrUpdateUserDto insertOrUpdateUserDto)
      throws ResourceNotFoundException, DuplicateUserException, UserWithNoRolesException {

    Set<Role> roles = new HashSet<>();
    for (RoleDto roleDto : insertOrUpdateUserDto.getRoles()) {
      roles.add(roleService.findById(roleDto.getId()));
    }

    validateUserHasAtLeastOneRole(roles);
    validateUserIsNotAlreadyRegistered(insertOrUpdateUserDto.getCpf());

    User user =
        User.builder()
            .name(insertOrUpdateUserDto.getName())
            .cpf(insertOrUpdateUserDto.getCpf())
            .password(bCryptPasswordEncoder.encode(insertOrUpdateUserDto.getPassword()))
            .roles(roles)
            .build();

    return UserDto.toDto(userRepository.save(user));
  }

  @Transactional
  public UserDto patch(InsertOrUpdateUserDto insertOrUpdateUserDto)
      throws ResourceNotFoundException {

    User user = findEntityById(insertOrUpdateUserDto.getId());
    user.setName(insertOrUpdateUserDto.getName());
    user.setPassword(bCryptPasswordEncoder.encode(insertOrUpdateUserDto.getPassword()));

    user = userRepository.save(user);
    return UserDto.builder()
        .id(user.getId())
        .roleDtos(Role.toDtos(user.getRoles()))
        .name(user.getName())
        .cpf(user.getCpf())
        .build();
  }

  @Transactional
  public void delete(Long id) throws ResourceNotFoundException, IllegalOperationException {
    User user = findEntityById(id);
    Account account = accountRepository.findByUserId(id).orElse(null);
    if (account != null) {
      if (account.getBalance() > 0.0) {
        throw new IllegalOperationException(
            "The user account is not empty and cannot be deleted. Empty the account first. accountId:"
                .concat(account.getId().toString()));
      }
      accountRepository.delete(account);
    }
    userRepository.delete(user);
  }

  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();
    return UserDto.toDtos(users);
  }

  public UserDto findById(Long id) throws ResourceNotFoundException {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "User not found. id:".concat(String.valueOf(id))));

    return UserDto.toDto(user);
  }

  public UserDto findByCpf(String cpf) throws ResourceNotFoundException {
    User user =
        userRepository
            .findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("User not found. cpf:".concat(cpf)));

    return UserDto.toDto(user);
  }

  public User findByUserName(String userName) throws ResourceNotFoundException {
    return userRepository
        .findByName(userName)
        .orElseThrow(
            () -> new ResourceNotFoundException("user not found. username:".concat(userName)));
  }

  public User findEntityById(Long id) throws ResourceNotFoundException {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "User not found. id:".concat(String.valueOf(id))));
    return user;
  }

  private void validateUserHasAtLeastOneRole(Set<Role> roles) throws UserWithNoRolesException {
    if (roles.isEmpty()) {
      throw new UserWithNoRolesException(
          "No valid roles found. Add at least one valid role to the user before insertion.");
    }
  }

  private void validateUserIsNotAlreadyRegistered(String cpf) throws DuplicateUserException {
    Optional<User> existingUser = userRepository.findByCpf(cpf);
    if (existingUser.isPresent()) {
      throw new DuplicateUserException(
          "A user with this CPF is already registered. cpf:".concat(cpf));
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByCpf(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("user not found. username:".concat(username)));
    return user;
  }
}
