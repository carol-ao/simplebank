package com.carol.simplebank.dto;

import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String cpf;

    private Set<RoleDto> roleDtos = new HashSet<RoleDto>();

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .cpf(user.getCpf())
                .roleDtos(Role.toDtos(user.getRoles()))
                .build();
    }

    public static List<UserDto> toDtos(List<User> users) {
        return users.stream()
                .map(
                        user ->
                                UserDto.builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .cpf(user.getCpf())
                                        .roleDtos(Role.toDtos(user.getRoles()))
                                        .build())
                .collect(Collectors.toList());
    }

}
