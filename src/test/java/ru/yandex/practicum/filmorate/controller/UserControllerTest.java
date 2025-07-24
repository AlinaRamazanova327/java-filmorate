package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserDBStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserController.class, UserService.class, UserDBStorage.class})
class UserControllerTest {
    private final UserController userController;

    static User getTestUser() {
        return User.builder()
                .id(1L)
                .email("example@example.com")
                .name("name")
                .login("login")
                .birthday(LocalDate.parse("2002-02-02"))
                .build();
    }

    static User getTestUser2() {
        return User.builder()
                .id(2L)
                .email("email@email.com")
                .name("name2")
                .login("login2")
                .birthday(LocalDate.parse("2000-02-02"))
                .build();
    }

    @Test
    void createUser() {
        User user = userController.createUser(getTestUser());
        assertThat(user)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(getTestUser());
    }

    @Test
    void updateUser() {
        User user = userController.createUser(getTestUser());
        user.setLogin("NewLogin");
        User updatedUser = userController.updateUser(user);
        assertThat(updatedUser)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user);
    }

    @Test
    void getUsers() {
        userController.createUser(getTestUser());
        userController.createUser(getTestUser2());
        List<User> userList = userController.getUsers();
        assertEquals(2, userList.size());
    }

    @Test
    void getUserById() {
        User user = getTestUser();
        userController.createUser(user);
        Optional<User> userOptional = userController.getUserById(user.getId());

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user);
    }

    @Test
    void addAndRemoveFriend() {
        User user = getTestUser();
        User user2 = getTestUser2();
        userController.createUser(getTestUser());
        userController.createUser(getTestUser2());
        userController.addFriend(user.getId(), user2.getId());
        List<User> friends = userController.getFriends(user.getId());
        assertFalse(friends.isEmpty());
        assertThat(friends.getFirst())
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user2);

        userController.removeFriend(user.getId(), user2.getId());
        List<User> friends2 = userController.getFriends(user.getId());
        assertTrue(friends2.isEmpty());
    }
}