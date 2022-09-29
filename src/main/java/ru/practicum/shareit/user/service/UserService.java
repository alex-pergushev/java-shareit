package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    /**
     * Добавление нового пользователя
     *
     * @param user пользователь
     */
    UserDto create(UserDto user);

    /**
     * Получение пользователя
     *
     * @param userId идентификатор пользователя
     * @return пользователь
     */
    UserDto findById(long userId) throws ObjectNotFoundException;

    /**
     * Получение всех пользователей
     *
     * @return пользователи
     */
    List<UserDto> findAll();

    /**
     * Обновление пользователя
     */
    UserDto update(long id, UserDto user) throws ObjectNotFoundException;

    /**
     * Удаление пользователя
     */
    void delete(long userId) throws ObjectNotFoundException;
}
