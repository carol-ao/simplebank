package com.carol.simplebank.controller;

import com.carol.simplebank.controller.user.UserController;
import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.RoleDto;
import com.carol.simplebank.dto.UserDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class UserControllerTest {

  @InjectMocks private UserController userController;

  @Mock private UserService userService;

  @Test
  public void mustReturnUserDtoAndHttpStatusCreatedWhenSaveNewUserWithValidData()
      throws UserWithNoRolesException, ResourceNotFoundException, DuplicateUserException {
    InsertOrUpdateUserDto insertOrUpdateUserDto = getValidUserDataToInsertOrUpdate();
    UserDto userDto = getValidUserDto();

    Mockito.when(userService.save(insertOrUpdateUserDto)).thenReturn(userDto);

    ResponseEntity<UserDto> response = userController.save(insertOrUpdateUserDto);

    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Assertions.assertEquals(insertOrUpdateUserDto.getCpf(), response.getBody().getCpf());
    Assertions.assertEquals(insertOrUpdateUserDto.getName(), response.getBody().getName());
    Assertions.assertTrue(
        response.getBody().getRoleDtos().containsAll(insertOrUpdateUserDto.getRoles()));
  }

  @Test
  public void mustReturnPageWithUsersInDatabaseAndStatusOk() {

    List<UserDto> userDtos = Arrays.asList(getValidUserDto(), getValidUserDto2());

    Pageable pageable = Pageable.ofSize(2);
    Mockito.when(userService.findAll(pageable)).thenReturn(new PageImpl<UserDto>(userDtos));

    ResponseEntity<Page<UserDto>> response = userController.findAll(pageable);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertTrue(response.getBody().getContent().containsAll(userDtos));
  }

  @Test
  public void mustReturnUserDataAndStatusOkWhenValidUserSearchedById()
      throws ResourceNotFoundException {
    UserDto userDto = getValidUserDto();
    Long userIdToSearch = userDto.getId();

    Mockito.when(userService.findById(userIdToSearch)).thenReturn(userDto);

    ResponseEntity<UserDto> response = userController.findById(userIdToSearch);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(userDto, response.getBody());
  }

  @Test
  public void mustReturnUserDataAndStatusOkWhenValidUserSearchedByCpf()
      throws ResourceNotFoundException {

    UserDto userDto = getValidUserDto();
    String cpfToSearch = userDto.getCpf();

    Mockito.when(userService.findByCpf(cpfToSearch)).thenReturn(userDto);

    ResponseEntity<UserDto> response = userController.findByCpf(cpfToSearch);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(userDto, response.getBody());
  }

  @Test
  public void mustReturnUserDataAndStatusOkWhenValidDataGivenToPatchUser()
      throws ResourceNotFoundException {

    InsertOrUpdateUserDto insertOrUpdateUserDto = getValidUserDataToInsertOrUpdate();
    UserDto userDto = getValidUserDto();

    Mockito.when(userService.patch(insertOrUpdateUserDto)).thenReturn(userDto);

    ResponseEntity<UserDto> response = userController.patch(insertOrUpdateUserDto);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(userDto, response.getBody());
  }

  @Test
  public void mustReturnStatusOkWhenValidDataGivenToDeleteUser() throws ResourceNotFoundException {

    Long userId = 1L;

    ResponseEntity response = userController.delete(1L);

    Mockito.verify(userService, Mockito.times(1)).delete(userId);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  private InsertOrUpdateUserDto getValidUserDataToInsertOrUpdate() {

    return InsertOrUpdateUserDto.builder()
        .id(1L)
        .password("123")
        .cpf("052.468.324-73")
        .name("Milly Alcock")
        .roles(new HashSet<RoleDto>(Collections.singleton(RoleDto.builder().id(1L).build())))
        .build();
  }

  private UserDto getValidUserDto() {
    return UserDto.builder()
        .id(1L)
        .cpf("052.468.324-73")
        .name("Milly Alcock")
        .roleDtos(new HashSet<RoleDto>(Collections.singleton(RoleDto.builder().id(1L).build())))
        .build();
  }

  private UserDto getValidUserDto2() {
    return UserDto.builder()
        .id(2L)
        .cpf("856.758.704-23")
        .name("Matt Smith")
        .roleDtos(new HashSet<RoleDto>(Collections.singleton(RoleDto.builder().id(1L).build())))
        .build();
  }
}
