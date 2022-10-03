package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * получение запросов по пользователям
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Добавление пользователя.
     */
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto user) {
        return userService.create(user);
    }

    /**
     * Получение пользователя
     */
    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable("userId") long userId) throws ObjectNotFoundException {
        return userService.findById(userId);
    }

    /**
     * Получение всех пользователей
     */
    @GetMapping()
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    /**
     * Обновление пользователя
     */
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") long userId,
                          @RequestBody UserDto user) throws ObjectNotFoundException {
        return userService.update(userId, user);
    }

    /**
     * Удаление пользователя
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) throws ObjectNotFoundException {
        userService.delete(userId);
    }
}
