package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private Map<Long, User> users = new HashMap<>();
    private long count = 1;

    @Override
    public UserDto add(UserDto user) {
        user.setId(count);
        User addUser = UserMapper.toUser(user);
        validateEmail(addUser);
        users.put(count++, addUser);
        log.debug("Создан новый пользователь {}", user.getId());
        return user;
    }

    @Override
    public User get(long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public Collection<UserDto> getAll() {
        List<UserDto> result = new ArrayList<>();
        for (User user : users.values()) {
            result.add(UserMapper.toUserDto(user));
        }
        return result;
    }

    @Override
    public UserDto update(UserDto user) {
        User updateUser = null;
        if (users.containsKey(user.getId())) {
            updateUser = users.get(user.getId());
            if (user.getName() != null) {
                updateUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                validateEmail(UserMapper.toUser(user));
                updateUser.setEmail(user.getEmail());
            }
        } else {
            throw new EntityNotFoundException("Попытка обновления несуществующего пользователя");
        }
        return UserMapper.toUserDto(updateUser);
    }

    @Override
    public void delete(long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
        } else {
            throw new EntityNotFoundException("Попытка удаления несуществующего пользователя");
        }
    }

    private void validateEmail(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email не может быть пустым");
        }

        for (User someUser : users.values()) {
            if (someUser.getId() != user.getId() && user.getEmail().equals(someUser.getEmail())) {
                throw new EntityAlreadyExistException(
                        String.format("Пользователь с Email %s уже существует", user.getEmail()));
            }
        }
    }
}
