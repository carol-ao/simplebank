package com.carol.simplebank.controller;

import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.UserDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.IllegalOperationException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

  @Autowired UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> save(@RequestBody InsertOrUpdateUserDto insertOrUpdateUserDto)
      throws UserWithNoRolesException, ResourceNotFoundException, DuplicateUserException {
    return new ResponseEntity(userService.save(insertOrUpdateUserDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return new ResponseEntity(userService.findAll(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> findById(@PathVariable(required = true, name = "id") Long id)
      throws ResourceNotFoundException {

    return ResponseEntity.ok(userService.findById(id));
  }

  @GetMapping(params = "cpf")
  public ResponseEntity<UserDto> findByCpf(@RequestParam(required = true, name = "cpf") String cpf)
      throws ResourceNotFoundException {

    return ResponseEntity.ok(userService.findByCpf(cpf));
  }

  @PatchMapping
  public ResponseEntity<UserDto> patch(@RequestBody InsertOrUpdateUserDto insertOrUpdateUserDto)
      throws ResourceNotFoundException {
    return new ResponseEntity(userService.patch(insertOrUpdateUserDto), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity delete(@PathVariable Long id)
      throws ResourceNotFoundException, IllegalOperationException {
    userService.delete(id);
    return ResponseEntity.ok().build();
  }
}
