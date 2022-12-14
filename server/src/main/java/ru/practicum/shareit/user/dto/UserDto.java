package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * отображение данных о пользователе
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;        // уникальный идентификатор пользователя
    private String name;    // имя или логин пользователя
    private String email;   // адрес электронной почты
    // (два пользователя не могут иметь одинаковый адрес электронной почты).
}
