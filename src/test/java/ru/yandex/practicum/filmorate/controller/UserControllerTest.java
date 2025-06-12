package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.groups.Default;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private UserController userController;
    private User user;
    @Autowired
    private Validator validator;

    private void validateObject(Object object) throws ValidationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object, Default.class);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "))
            );
        }
    }

    @BeforeEach
    void setUp() {
        userController = new UserController();
        userController.getUsers().clear();
        user = User.builder()
                .email("example@example.com")
                .login("login")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
    }

    @Test
    void testCreateValidUser() throws ValidationException {
        User createdUser = userController.createUser(user);
        assertEquals("example@example.com", createdUser.getEmail());
        assertEquals("login", createdUser.getLogin());
        assertEquals(LocalDate.of(1991, 1, 1), createdUser.getBirthday());
        assertEquals(1, createdUser.getId());
    }

    @Test
    void testCreateInvalidUser() throws ValidationException {
        User invalidUser1 = User.builder()
                .email("")
                .login("login1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        assertThrows(ValidationException.class, () -> validateObject(invalidUser1));
        User invalidUser2 = User.builder()
                .email("invalid_email_format")
                .login(" ")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        assertThrows(ValidationException.class, () -> validateObject(invalidUser2));
        User invalidUser3 = User.builder()
                .email("valid@email.com")
                .login("valid_login")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        assertThrows(ValidationException.class, () -> validateObject(invalidUser3));
    }

    @Test
    void testUpdateValidUser() throws ValidationException {
        userController.createUser(user);
        User updatedUser = User.builder()
                .id(user.getId())
                .email("example@example.com")
                .login("login2")
                .birthday(LocalDate.of(1992, 1, 1))
                .build();
        User result = userController.updateUser(updatedUser);
        assertEquals("example@example.com", result.getEmail());
        assertEquals("login2", result.getLogin());
        assertEquals(LocalDate.of(1992, 1, 1), result.getBirthday());
    }

    @Test
    void testUpdateNonExistingUser() throws ValidationException {
        userController.createUser(user);
        User updatedUser = User.builder()
                .id(465L)
                .email("example@example.com")
                .login("login")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        assertThrows(NotFoundException.class, () -> userController.updateUser(updatedUser));
    }

    @Test
    void testGetUsers() {
        List<User> emptyList = new ArrayList<>(userController.getUsers());
        assertEquals(0, emptyList.size());
        userController.createUser(user);
        List<User> users = new ArrayList<>(userController.getUsers());
        assertEquals(1, users.size());
    }
}