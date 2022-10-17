package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column
    private String email;   // адрес электронной почты
    // (два пользователя не могут иметь одинаковый адрес электронной почты).
}
