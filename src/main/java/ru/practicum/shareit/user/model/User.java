package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * пользователь
 */
@Data
@Builder
public class User {
    @NotNull
    private long id;        // уникальный идентификатор пользователя

    @NotNull
    private String name;    // имя или логин пользователя

    @NotNull
    @Email
    private String email;   // адрес электронной почты
                            // (два пользователя не могут иметь одинаковый адрес электронной почты).

}
