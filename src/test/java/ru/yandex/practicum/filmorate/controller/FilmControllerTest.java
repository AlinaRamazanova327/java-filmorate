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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {
    @Autowired
    private FilmController filmController;
    private Film film;
    @Autowired
    private Validator validator;

    private void validateObject(Object object) throws ValidationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object, Default.class);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", ")));
        }
    }

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(2020, 2, 2))
                .duration(147)
                .build();
    }

    @Test
    void testCreateValidFilm() throws ValidationException {
        Film result = filmController.createFilm(film);
        assertEquals("film", result.getName());
        assertEquals(147, result.getDuration());
        assertEquals("description", result.getDescription());
        assertEquals(LocalDate.of(2020, 2, 2), result.getReleaseDate());
        assertTrue(result.getId() > 0);
    }

    @Test
    void testCreateInvalidFilm() throws ValidationException {
        Film film1 = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2020, 2, 2))
                .duration(147)
                .build();
        assertThrows(ValidationException.class, () -> validateObject(film1));
        Film film2 = Film.builder()
                .name("film")
                .description("a".repeat(201))
                .releaseDate(LocalDate.of(2020, 2, 2))
                .duration(147)
                .build();
        assertThrows(ValidationException.class, () -> validateObject(film2));
        Film film3 = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(147)
                .build();
        assertThrows(ValidationException.class, () -> validateObject(film3));
        Film film4 = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(2020, 2, 2))
                .duration(0)
                .build();
        assertThrows(ValidationException.class, () -> validateObject(film4));
    }

    @Test
    void testUpdateValidFilm() throws ValidationException {
        filmController.createFilm(film);
        Film updatedFilm = Film.builder()
                .id(film.getId())
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(2021, 2, 2))
                .duration(149)
                .build();
        Film result = filmController.updateFilm(updatedFilm);
        assertEquals("film2", result.getName());
        assertEquals(149, result.getDuration());
        assertEquals("description2", result.getDescription());
        assertEquals(LocalDate.of(2021, 2, 2), result.getReleaseDate());
    }

    @Test
    void testUpdateNonExistingFilm() throws ValidationException {
        filmController.createFilm(film);
        Film updatedFilm = Film.builder()
                .id(23L)
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(2021, 2, 2))
                .duration(149)
                .build();
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(updatedFilm));
    }

    @Test
    void testGetFilms() {
        List<Film> emptyList = new ArrayList<>(filmController.getFilms());
        assertEquals(0, emptyList.size());
        filmController.createFilm(film);
        List<Film> films = new ArrayList<>(filmController.getFilms());
        assertEquals(1, films.size());
    }
}