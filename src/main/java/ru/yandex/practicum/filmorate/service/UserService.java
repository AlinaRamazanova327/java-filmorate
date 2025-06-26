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
        return userStorage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));
        User friend = getUserById(friendId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));
        User friend = getUserById(friendId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getUserFriends(Long userId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));
        User friend = getUserById(otherId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не найден"));

        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .toList();
    }
}