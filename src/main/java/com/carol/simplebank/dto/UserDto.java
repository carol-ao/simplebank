package com.carol.simplebank.dto;

import com.carol.simplebank.model.Role;
import com.carol.simplebank.model.User;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    private Long accountId;

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .cpf(user.getCpf())
                .roleDtos(Role.toDtos(user.getRoles()))
                .accountId(user.getAccount() != null ? user.getAccount().getId() : null)
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
                                        .accountId(user.getAccount() != null ? user.getAccount().getId() : null)
                                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) && Objects.equals(name, userDto.name) && Objects.equals(cpf, userDto.cpf) && Objects.equals(roleDtos, userDto.roleDtos) && Objects.equals(accountId, userDto.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
