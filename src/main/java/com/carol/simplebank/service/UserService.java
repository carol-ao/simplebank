package com.carol.simplebank.service;

import com.carol.simplebank.dto.RoleDto;
import com.carol.simplebank.dto.UserGetDto;
import com.carol.simplebank.dto.UserPostDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleService roleService;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  public UserGetDto save(UserPostDto userPostDto)
          throws ResourceNotFoundException, DuplicateUserException, UserWithNoRolesException {

    Set<Role> roles = new HashSet<>();
    for (RoleDto roleDto : userPostDto.getRoles()) {
      roles.add(roleService.findById(roleDto.getId()));
    }

    validateUserHasAtLeastOneRole(roles);
    validateUserIsNotAlreadyRegistered(userPostDto.getCpf());

    User user =
        User.builder()
            .name(userPostDto.getName())
            .cpf(userPostDto.getCpf())
            .password(bCryptPasswordEncoder.encode(userPostDto.getPassword()))
            .roles(roles)
            .build();

    return UserGetDto.toDto(userRepository.save(user));
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
      throw new DuplicateUserException("A user with this CPF is already registered.");
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByCpf(username).orElseThrow( () -> new UsernameNotFoundException("user not found."));
    return user;
  }
}
