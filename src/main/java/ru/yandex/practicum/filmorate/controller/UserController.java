package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private Long userCounter = 1L;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(userCounter++);
        user.setName(user.getDisplayName());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws NotFoundException {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь с указанным id не найден");
        }
        User existingUser = users.get(user.getId());
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getDisplayName());
        existingUser.setBirthday(user.getBirthday());
        log.info("Данные пользователя обновлены: {}", existingUser);
        return existingUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }
}