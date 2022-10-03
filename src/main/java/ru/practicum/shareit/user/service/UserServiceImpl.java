package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private static final String NOT_FOUND = "Не найден пользователь ";

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> findAll() {
        log.debug("Поиск всех пользователей");
        return userStorage.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(long id) throws ObjectNotFoundException {
        log.debug("Поиск пользователя с идентификатором: {}", id);
        return UserMapper.toUserDto(userStorage.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id)));
    }

    @Override
    public UserDto create(UserDto user) {
        log.debug("Создание нового пользователя с идентификатором: {}", user.getId());
        return UserMapper.toUserDto(userStorage.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto update(long id, UserDto userUpdate) throws ObjectNotFoundException {
        User user = userStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
        if (userUpdate.getName() != null && !userUpdate.getName().isBlank()) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null && !userUpdate.getEmail().isBlank()) {
            user.setEmail(userUpdate.getEmail());
        }
        log.debug("Изменение пользователя с идентификатором: {}", user.getId());
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
    public void delete(long userId) throws ObjectNotFoundException {
        log.debug("Удаление пользователя с идентификатором: {}", userId);
        userStorage.deleteById(userId);
    }
}
