package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) throws NotFoundException {
        return getUserById(user.getId())
                .map(u -> userStorage.updateUser(user))
                .orElseThrow(() -> new NotFoundException("Пользователь с указанным id не найден"));
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userStorage.getUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден")));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));
        User friend = getUserById(friendId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));
        User friend = getUserById(friendId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));
        User friend = getUserById(friendId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        return userStorage.getCommonFriends(userId, friendId);
    }
}