package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    Long id;
    @NotBlank(message = "название не может быть пустым")
    String name;
    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    String description;
    LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительным числом")
    int duration;

    @AssertTrue(message = "Дата релиза должна быть не раньше 28 декабря 1895 года.")
    boolean isValidReleaseDate() {
        return this.releaseDate != null && !this.releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }
}