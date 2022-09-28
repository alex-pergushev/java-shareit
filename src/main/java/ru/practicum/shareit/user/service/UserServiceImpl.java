package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto add(UserDto user) {
        User addUser = UserMapper.toUser(user);
        validateEmail(addUser);
        addUser = repository.saveAndFlush(UserMapper.toUser(user));
        log.debug("Создан новый пользователь {}", user.getId());
        return UserMapper.toUserDto(addUser);
    }

    @Override
    public UserDto getDto(long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }

    @Override
    public User getUser(long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public Collection<UserDto> getAll() {
        List<UserDto> result = new ArrayList<>();
        for (User user : repository.findAll()) {
            result.add(UserMapper.toUserDto(user));
        }
        return result;
    }

    @Override
    public UserDto update(UserDto user) {
        Optional<User> updateUser = null;

        if(user != null && user.getId() != null) {
            updateUser = repository.findById(user.getId());
        }

        if(updateUser.isPresent()) {
            if(user.getName() != null) {
                updateUser.get().setName(user.getName());
            }
            if(user.getEmail() != null) {
                validateEmail(UserMapper.toUser(user));
                updateUser.get().setEmail(user.getEmail());
            }
            repository.save(updateUser.get());
        } else {
            throw new EntityNotFoundException("Попытка обновления несуществующего пользователя");
        }
        return UserMapper.toUserDto(updateUser.get());
    }

    @Override
    public void delete(long userId) {
        if (repository.existsById(userId)) {
            repository.deleteById(userId);
        } else {
            throw new EntityNotFoundException("Попытка удаления несуществующего пользователя");
        }
    }

    private void validateEmail(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email не может быть пустым");
        }
    }
}
