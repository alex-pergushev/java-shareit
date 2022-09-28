package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    /**
     * Добавление нового пользователя
     * @param user пользователь
     */
    UserDto add(UserDto user);

    /**
     * Получение пользователя
     * @param userId идентификатор пользователя
     * @return пользователь
     */
    UserDto getDto(long userId);
    User getUser(long userId);

    /**
     * Получение всех пользователей
     * @return пользователи
     */
    Collection<UserDto> getAll();

    /**
     * Обновление пользователя
     */
    UserDto update(UserDto user);

    /**
     * Удаление пользователя
     */
    void delete(long userId);
}
