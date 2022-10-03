package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * отображение данных о пользователе
 */

@Data
@Builder
public class UserDto {
    private long id;        // уникальный идентификатор пользователя
    private String name;    // имя или логин пользователя
    @Email
    @NotNull
    private String email;   // адрес электронной почты
    // (два пользователя не могут иметь одинаковый адрес электронной почты).
}
