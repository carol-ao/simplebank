package com.carol.simplebank.service;

import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.RoleDto;
import com.carol.simplebank.dto.UserDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.factory.UserFactory;
import com.carol.simplebank.model.Account;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import com.carol.simplebank.repositories.AccountRepository;
import com.carol.simplebank.repositories.UserRepository;
import com.carol.simplebank.service.role.RoleService;
import com.carol.simplebank.service.user.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {

  @InjectMocks private UserServiceImpl userService;

  @Mock private UserRepository userRepository;

  @Mock private RoleService roleService;

  @Mock private AccountRepository accountRepository;

  @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Test
  public void mustSaveNewUserAndReturnDtoWhenValidUserDataGiven()
      throws UserWithNoRolesException, ResourceNotFoundException, DuplicateUserException {

    InsertOrUpdateUserDto insertOrUpdateUserDto = UserFactory.getValidInsertOrUpdateUserDto();
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
  public void mustThrowUserWithNoRolesExceptionWhenNoRoleIsGivenToAssignToNewUser()
      throws ResourceNotFoundException {
    InsertOrUpdateUserDto insertOrUpdateUserDto = UserFactory.getValidInsertOrUpdateUserDto();
    insertOrUpdateUserDto.setRoles(null);

    Exception exception =
        Assertions.assertThrows(
            UserWithNoRolesException.class, () -> userService.save(insertOrUpdateUserDto));
    Mockito.verify(roleService, Mockito.times(0)).findById(Mockito.any());

    Assertions.assertEquals(
        "No valid roles found. Add at least one valid role to the user before insertion.",
        exception.getMessage());
  }

  @Test
  public void
      mostThrowDuplicateUserExceptionWhenNewUserSaveAttemptedAndUserWithGivenCpfAlreadyExists() {
    InsertOrUpdateUserDto insertOrUpdateUserDto = UserFactory.getValidInsertOrUpdateUserDto();
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

    InsertOrUpdateUserDto insertOrUpdateUserDto = UserFactory.getValidInsertOrUpdateUserDto();
    Set<Role> roles = RoleDto.toRoles(insertOrUpdateUserDto.getRoles());
    String newName = insertOrUpdateUserDto.getName();
    String newPassword = "newEncryptedPassword";
    String oldName = "Emma Darcy";
    String oldPassword = "oldEncryptedPassword";

    User user =
        User.builder()
            .id(insertOrUpdateUserDto.getId())
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

    Assertions.assertEquals(newName, userDto.getName());
    Assertions.assertNotEquals(oldName, userDto.getName());

    Assertions.assertEquals(newPassword, user.getPassword());
    Assertions.assertNotEquals(oldPassword, user.getPassword());
  }

  @Test
  public void mustDeleteUserAndAccountWhenDeleteValidUserWithAnAccountAttempted()
      throws ResourceNotFoundException {
    User user = UserFactory.getUser1WithAccount();
    Account account = user.getAccount();

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    userService.delete(user.getId());

    Mockito.verify(accountRepository, Mockito.times(1)).delete(account);
    Mockito.verify(userRepository, Mockito.times(1)).delete(user);
  }

  @Test
  public void mustDeleteUserWhenDeleteValidUserWithoutAnAccountAttempted()
      throws ResourceNotFoundException {
    User user = UserFactory.getUser1();

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    Mockito.when(accountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

    userService.delete(user.getId());

    Mockito.verify(userRepository, Mockito.times(1)).delete(user);
  }

  @Test
  public void mustReturnPageOfUserDtoWithUserData() {
    List<User> users = Arrays.asList(UserFactory.getUser1(), UserFactory.getUser2());
    Pageable pageable = Pageable.ofSize(2).withPage(0);
    Page<User> page = new PageImpl<>(users, pageable, users.size());
    Mockito.when(userRepository.findAll(pageable)).thenReturn(page);

    Page<UserDto> userDtosPage = userService.findAll(pageable);

    Assertions.assertEquals(2, userDtosPage.getSize());
    Assertions.assertTrue(
        userDtosPage.get().collect(Collectors.toList()).containsAll(UserDto.toDtos(users)));
  }

  @Test
  public void mustReturnUserDtoWithUserDataWhenFindByIdAttemptedWithExistingUser()
      throws ResourceNotFoundException {

    User user = UserFactory.getUser1();

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    UserDto userDto = userService.findById(user.getId());

    Assertions.assertEquals(user.getId(), userDto.getId());
    Assertions.assertEquals(user.getName(), userDto.getName());
    Assertions.assertEquals(user.getCpf(), userDto.getCpf());
    Set<Role> roles = RoleDto.toRoles(userDto.getRoleDtos());
    Assertions.assertTrue(roles.containsAll(user.getRoles()));
  }

  // TODO: tests for findByCpf, findByUserName, findEntityById, loadUserByUsername

}
