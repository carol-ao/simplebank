package com.carol.simplebank.factory;

import com.carol.simplebank.dto.InsertOrUpdateUserDto;
import com.carol.simplebank.dto.RoleDto;
import com.carol.simplebank.model.Role;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RoleFactory {

    public static Role getAdminRole(){
        return Role.builder()
                .id(1L)
                .authority("ROLE_ADMIN")
                .build();
    }

    public static Role getOperatorRole(){
        return Role.builder()
                .id(2L)
                .authority("ROLE_OPERATOR")
                .build();
    }

    public static Set<Role> getBasicRoles(){
        return new HashSet<>(Arrays.asList(getAdminRole(),getOperatorRole()));
    }

    public static RoleDto getAdminRoleDto(){

        return RoleDto.builder()
                .id(1L)
                .authority("ROLE_ADMIN")
                .build();

    }


    public static Set<RoleDto> getBasicRoleDtos() {
        return Role.toDtos(getBasicRoles());
    }
}
