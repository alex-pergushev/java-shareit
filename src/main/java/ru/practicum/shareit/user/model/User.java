package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * пользователь
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;        // уникальный идентификатор пользователя

    @Column
    private String name;    // имя или логин пользователя

    @NotBlank(message = "Не указан email пользователя!")
    @Email(message = "Не валидный email пользователя!")
    @Column
    private String email;   // адрес электронной почты
    // (два пользователя не могут иметь одинаковый адрес электронной почты).

    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
    }

}
