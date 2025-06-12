package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Builder
@Data
public class User {
    Long id;
    @NotBlank(message = "email не может быть пустым или содержать пробелы")
    @Email(message = "неверный формат электронной почты")
    String email;
    @NotBlank(message = "логин не может быть пустым и содержать пробелы")
    String login;
    String name;
    @PastOrPresent(message = "дата рождения не может быть в будущем")
    LocalDate birthday;

    public String getDisplayName() {
        return (name != null && !name.trim().isEmpty()) ? name : login;
    }
}