package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import javax.validation.constraints.*;

/**
 * Класс пользователя
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;        //уникальный идентификатор пользователя

    @NotNull
    private String name;    //имя или логин пользователя

    @NotNull
    @Email
    private String email;   //адрес электронной почты
                            //(два пользователя не могут иметь одинаковый адрес электронной почты).
}
