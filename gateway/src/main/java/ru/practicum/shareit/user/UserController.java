package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable("id") long id) {
        log.info("Get by id {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto user) {
        log.info("Creating user {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта пользователя не может быть пустой.");
        }
        return userClient.create(user);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @Valid @RequestBody UserDto user) {
        log.info("Updating user {}, userId={}", user, id);
        return userClient.update(id, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        log.info("Deleting userId {}", userId);
        userClient.deleteUser(userId);
    }
}
