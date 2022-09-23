package com.carol.simplebank.controller;

import com.carol.simplebank.dto.UserGetDto;
import com.carol.simplebank.dto.UserPostDto;
import com.carol.simplebank.exceptions.DuplicateUserException;
import com.carol.simplebank.exceptions.ResourceNotFoundException;
import com.carol.simplebank.exceptions.UserWithNoRolesException;
import com.carol.simplebank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<UserGetDto> save(@RequestBody UserPostDto userPostDto) throws UserWithNoRolesException, ResourceNotFoundException, DuplicateUserException {
            return new ResponseEntity(userService.save(userPostDto),HttpStatus.CREATED);
    }

}
