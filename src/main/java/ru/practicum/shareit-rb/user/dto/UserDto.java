package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * Класс для отображении данных о пользователе
 */
@Data
@Builder
public class UserDto {
    private Long id;        //уникальный идентификатор пользователя
    private String name;    //имя или логин пользователя
    @Email(message = "Некорректный Email!")
    private String email;   //адрес электронной почты
                            //(два пользователя не могут иметь одинаковый адрес электронной почты).
}
