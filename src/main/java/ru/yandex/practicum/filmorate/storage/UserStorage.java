package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStorage {

    User createUser(User user);

    User updateUser(User user) throws NotFoundException;

    List<User> getUsers();

    Optional<User> getUserById(Long id);

    List<User> getFriends(Long id);

    void addFriend(Long user_id, Long friend_id);

    void removeFriend(Long user_id, Long friend_id);

    List<User> getCommonFriends(Long userId, Long friendId);
}