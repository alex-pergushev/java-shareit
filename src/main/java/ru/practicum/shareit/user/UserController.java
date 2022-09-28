package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * получение запросов по пользователям
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Добавление пользователя.
     */
    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto user) {
        return userService.add(user);
    }

    /**
     * Получение пользователя
     */
    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable long userId) {
        return userService.getDto(userId);
    }

    /**
     * Получение всех пользователей
     */
    @GetMapping()
    public Collection<UserDto> findAllUser() {
        return userService.getAll();
    }

    /**
     * Обновление пользователя
     */
    @PatchMapping("/{userId}")
    public UserDto patchUser(@Valid @RequestBody UserDto user,
                             @PathVariable long userId) {
        user.setId(userId);
        return userService.update(user);
    }

    /**
     * Удаление пользователя
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.delete(userId);
    }
}
