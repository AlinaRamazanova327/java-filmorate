/*package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Deprecated
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        return users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с указанным id не найден");
        }
        users.put(user.getId(), user);
        return user;
    }
}*/