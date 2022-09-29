package com.carol.simplebank.factory;

import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.LoginForm;
import com.carol.simplebank.dto.RoleDto;
import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;

import java.util.Collections;
import java.util.HashSet;

public class UserFactory {

    public static User getUser1() {
        return User.builder()
                .id(1L)
                .password("encrypted_password")
                .cpf("052.468.324-73")
                .name("Milly Alcock")
                .roles(new HashSet<Role>(Collections.singleton(RoleFactory.getAdminRole())))
                .build();
    }

    public static User getUser2() {
        return User.builder()
                .id(2L)
                .password("encrypted_password")
                .cpf("856.758.704-23")
                .name("Matt Smith")
                .roles(new HashSet<Role>(Collections.singleton(RoleFactory.getOperatorRole())))
                .build();
    }

    public static User getUser1WithAccount() {
        return User.builder()
                .id(1L)
                .password("encrypted_password")
                .cpf("052.468.324-73")
                .name("Milly Alcock")
                .account(AccountFactory.getAccountWithBigValueBalanceForUser1())
                .roles(new HashSet<Role>(Collections.singleton(RoleFactory.getAdminRole())))
                .build();
    }

    public static InsertOrUpdateUserDto getValidInsertOrUpdateUserDto() {

        return InsertOrUpdateUserDto.builder()
                .password("123")
                .roles(RoleFactory.getBasicRoleDtos())
                .cpf("052.468.324-73")
                .name("Milly Alcock")
                .build();
    }

    public static LoginForm getValidUserLoginFormForUser1() {
        return LoginForm.builder().password("123").username("Milly Alcock").build();
    }

    public static User toUser(InsertOrUpdateUserDto insertOrUpdateUserDto) {
    return User.builder()
        .id(insertOrUpdateUserDto.getId())
        .name(insertOrUpdateUserDto.getName())
        .cpf(insertOrUpdateUserDto.getCpf())
        .password("encrypted_password")
        .roles(RoleDto.toRoles(insertOrUpdateUserDto.getRoles()))
        .build();
    }
}
