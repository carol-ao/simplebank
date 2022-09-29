package com.carol.simplebank.service.user;

import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.RoleDto;
import com.carol.simplebank.dto.UserDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.model.Account;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.AccountRepository;
import com.carol.simplebank.repositories.UserRepository;
import com.carol.simplebank.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private RoleService roleService;

  @Autowired private AccountRepository accountRepository;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDto save(InsertOrUpdateUserDto insertOrUpdateUserDto)
      throws ResourceNotFoundException, DuplicateUserException, UserWithNoRolesException {

    Set<Role> roles = validateAndGetNewUserRoles(insertOrUpdateUserDto.getRoles());
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

  private Set<Role> validateAndGetNewUserRoles(Set<RoleDto> roleDtos)
      throws UserWithNoRolesException, ResourceNotFoundException {
    Set<Role> roles = new HashSet<>();
    if (roleDtos != null) {
      for (RoleDto roleDto : roleDtos) {
        roles.add(roleService.findById(roleDto.getId()));
      }
    } else {
      throw new UserWithNoRolesException(
          "No valid roles found. Add at least one valid role to the user before insertion.");
    }
    return roles;
  }

  private void validateUserIsNotAlreadyRegistered(String cpf) throws DuplicateUserException {
    Optional<User> existingUser = userRepository.findByCpf(cpf);
    if (existingUser.isPresent()) {
      throw new DuplicateUserException(
          "A user with this CPF is already registered. cpf:".concat(cpf));
    }
  }

  @Override
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
        .accountId(user.getAccount() != null ? user.getAccount().getId() : null)
        .build();
  }

  @Override
  public void delete(Long id) throws ResourceNotFoundException {
    User user = findEntityById(id);
    Account account = user.getAccount();
    if (account != null) {
      accountRepository.delete(account);
    }
    userRepository.delete(user);
  }

  @Override
  public Page<UserDto> findAll(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    return users.map(UserDto::toDto);
  }

  @Override
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

  @Override
  public UserDto findByCpf(String cpf) throws ResourceNotFoundException {
    User user =
        userRepository
            .findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("User not found. cpf:".concat(cpf)));

    return UserDto.toDto(user);
  }

  @Override
  public User findByUserName(String userName) throws ResourceNotFoundException {
    return userRepository
        .findByName(userName)
        .orElseThrow(
            () -> new ResourceNotFoundException("user not found. username:".concat(userName)));
  }

  @Override
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
