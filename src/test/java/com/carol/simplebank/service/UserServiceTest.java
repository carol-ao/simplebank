package com.carol.simplebank.service;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

  @InjectMocks private UserService userService;

  @Mock private UserRepository userRepository;

  @Mock private RoleService roleService;

  @Mock private AccountRepository accountRepository;

  @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Test
  public void mustSaveNewUserAndReturnDtoWhenValidUserDataGiven()
      throws UserWithNoRolesException, ResourceNotFoundException, DuplicateUserException {

    InsertOrUpdateUserDto insertOrUpdateUserDto = getValidUserData();
    Set<Role> roles = RoleDto.toRoles(insertOrUpdateUserDto.getRoles());
    User user =
        User.builder()
            .id(1L)
            .name(insertOrUpdateUserDto.getName())
            .cpf(insertOrUpdateUserDto.getCpf())
            .password("encrypted_password")
            .roles(roles)
            .build();

    Mockito.when(userRepository.findByCpf(insertOrUpdateUserDto.getCpf()))
        .thenReturn(Optional.empty());
    Mockito.when(bCryptPasswordEncoder.encode(insertOrUpdateUserDto.getPassword()))
        .thenReturn("encrypted_password");
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
    Mockito.when(roleService.findById(Mockito.any())).thenReturn(Role.builder().id(1L).build());
    UserDto userDto = userService.save(insertOrUpdateUserDto);

    Assertions.assertNotNull(userDto.getId());
    Assertions.assertEquals(insertOrUpdateUserDto.getRoles(), userDto.getRoleDtos());
    Assertions.assertEquals(insertOrUpdateUserDto.getCpf(), userDto.getCpf());
    Assertions.assertEquals(insertOrUpdateUserDto.getName(), userDto.getName());
  }

  @Test
  public void mustThrowUserWithNoRolesExceptionWhenNoRoleIsGivenToAssignToNewUser() {
    InsertOrUpdateUserDto insertOrUpdateUserDto = getUserDataWithNoRolesToSaveNewUser();
    Exception exception =
        Assertions.assertThrows(
            UserWithNoRolesException.class, () -> userService.save(insertOrUpdateUserDto));
    Assertions.assertEquals(
        "No valid roles found. Add at least one valid role to the user before insertion.",
        exception.getMessage());
  }

  @Test
  public void
      mostThrowDuplicateUserExceptionWhenNewUserSaveAttemptedAndUserWithGivenCpfAlreadyExists() {
    InsertOrUpdateUserDto insertOrUpdateUserDto = getValidUserData();
    Set<Role> roles = RoleDto.toRoles(insertOrUpdateUserDto.getRoles());
    User user =
        User.builder()
            .id(1L)
            .name(insertOrUpdateUserDto.getName())
            .cpf(insertOrUpdateUserDto.getCpf())
            .password("encrypted_password")
            .roles(roles)
            .build();

    Mockito.when(userRepository.findByCpf(insertOrUpdateUserDto.getCpf()))
        .thenReturn(Optional.of(user));

    Exception exception =
        Assertions.assertThrows(
            DuplicateUserException.class, () -> userService.save(insertOrUpdateUserDto));
    Assertions.assertEquals(
        "A user with this CPF is already registered. cpf:".concat(user.getCpf()),
        exception.getMessage());
  }

  @Test
  public void mustUpdateUserNameAndPasswordAndReturnDtoWhenValidUserDataIsGivenToPatch()
      throws ResourceNotFoundException {

    InsertOrUpdateUserDto insertOrUpdateUserDto = getValidUserData();
    Set<Role> roles = RoleDto.toRoles(insertOrUpdateUserDto.getRoles());
    String oldName = "Emma Darcy";
    String oldPassword = "encrypted_password";

    User user =
        User.builder()
            .id(1L)
            .name(oldName)
            .cpf(insertOrUpdateUserDto.getCpf())
            .password(oldPassword)
            .roles(roles)
            .build();

    Mockito.when(userRepository.findById(insertOrUpdateUserDto.getId()))
        .thenReturn(Optional.of(user));
    Mockito.when(bCryptPasswordEncoder.encode(Mockito.anyString()))
        .thenReturn("newEncryptedPassword");
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

    UserDto userDto = userService.patch(insertOrUpdateUserDto);

    Assertions.assertEquals(insertOrUpdateUserDto.getId(), userDto.getId());
    Assertions.assertEquals(insertOrUpdateUserDto.getCpf(), userDto.getCpf());
    Assertions.assertEquals(insertOrUpdateUserDto.getName(), userDto.getName());
    Assertions.assertFalse(oldPassword.equals(user.getPassword()));
  }

  @Test
  public void mustDeleteUserAndAccountWhenDeleteValidUserWithAnAccountAttempted()
      throws ResourceNotFoundException {
    User user = getUser1();
    Account account = getEmptyAccountForUser(user);

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.of(account));

    userService.delete(user.getId());

    Mockito.verify(accountRepository, Mockito.times(1)).delete(account);
    Mockito.verify(userRepository, Mockito.times(1)).delete(user);
  }

  @Test
  public void mustDeleteUserWhenDeleteValidUserWithoutAnAccountAttempted()
      throws ResourceNotFoundException {
    User user = getUser1();

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

    userService.delete(user.getId());

    Mockito.verify(userRepository, Mockito.times(1)).delete(user);
  }

  @Test
  public void mustReturnListOfUserDtoWithAllUsersData() {
    List<User> users = Arrays.asList(getUser1(), getUser2());

    Mockito.when(userRepository.findAll()).thenReturn(users);

    List<UserDto> userDtos = userService.findAll();

    userDtos.forEach(
        userDto -> {
          Assertions.assertEquals(users.get(userDtos.indexOf(userDto)).getId(), userDto.getId());
          Assertions.assertEquals(users.get(userDtos.indexOf(userDto)).getCpf(), userDto.getCpf());
          Assertions.assertEquals(
              users.get(userDtos.indexOf(userDto)).getName(), userDto.getName());
          Set<Role> roles = RoleDto.toRoles(userDto.getRoleDtos());
          Assertions.assertTrue(roles.containsAll(users.get(userDtos.indexOf(userDto)).getRoles()));
        });
  }

  @Test
  public void mustReturnUserDtoWithUserDataWhenFindByIdAttemptedWithExistingUser()
      throws ResourceNotFoundException {

    User user = getUser1();

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    UserDto userDto = userService.findById(user.getId());

    Assertions.assertEquals(user.getId(), userDto.getId());
    Assertions.assertEquals(user.getName(), userDto.getName());
    Assertions.assertEquals(user.getCpf(), userDto.getCpf());
    Set<Role> roles = RoleDto.toRoles(userDto.getRoleDtos());
    Assertions.assertTrue(roles.containsAll(user.getRoles()));
  }

  // TODO: tests for findByCpf, findByUserName, findEntityById, loadUserByUsername
  // TODO: use factory desing pattern for dtos and entities

  private InsertOrUpdateUserDto getUserDataWithNoRolesToSaveNewUser() {

    return InsertOrUpdateUserDto.builder()
        .password("123")
        .cpf("052.468.324-73")
        .name("Milly Alcock")
        .build();
  }

  private InsertOrUpdateUserDto getValidUserData() {

    return InsertOrUpdateUserDto.builder()
        .id(1L)
        .password("123")
        .cpf("052.468.324-73")
        .name("Milly Alcock")
        .roles(new HashSet<RoleDto>(Collections.singleton(RoleDto.builder().id(1L).build())))
        .build();
  }

  private User getUser1() {
    return User.builder()
        .id(1L)
        .password("123")
        .cpf("052.468.324-73")
        .name("Milly Alcock")
        .roles(new HashSet<Role>(Collections.singleton(Role.builder().id(1L).build())))
        .build();
  }

  private User getUser2() {
    return User.builder()
        .id(1L)
        .password("456")
        .cpf("856.758.704-23")
        .name("Matt Smith")
        .roles(new HashSet<Role>(Collections.singleton(Role.builder().id(1L).build())))
        .build();
  }

  private Account getEmptyAccountForUser(User user) {
    return Account.builder().id(1L).user(user).balance(0).build();
  }
}
