package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.UserDBStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmController.class, FilmService.class, FilmDBStorage.class, MpaService.class, MpaDBStorage.class,
        GenreService.class, GenreDBStorage.class, UserDBStorage.class})
class FilmControllerTest {
    private final FilmController filmController;

    static Film getTestFilm() {
        return Film.builder()
                .id(1L)
                .name("film")
                .description("desc")
                .duration(111)
                .releaseDate(LocalDate.parse("2002-02-20"))
                .mpa(Mpa.builder().id(3).name("PG-13").build())
                .build();
    }

    static Film getTestFilm2() {
        return Film.builder()
                .id(2L)
                .name("film2")
                .description("desc2")
                .duration(120)
                .releaseDate(LocalDate.parse("2000-02-20"))
                .mpa(Mpa.builder().id(2).name("PG").build())
                .build();
    }

    @Test
    void createFilm() {
        Film film = filmController.createFilm(getTestFilm());
        assertThat(film)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(getTestFilm());
    }

    @Test
    void updateFilm() {
        Film film = filmController.createFilm(getTestFilm());
        film.setName("NewFilm");
        Film updatedFilm = filmController.updateFilm(film);
        assertThat(updatedFilm)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(film);
    }

    @Test
    void getFilms() {
        filmController.createFilm(getTestFilm());
        filmController.createFilm(getTestFilm2());
        List<Film> filmList = filmController.getFilms();
        assertEquals(2, filmList.size());
    }

    @Test
    void getFilmById() {
        Film film = getTestFilm();
        filmController.createFilm(film);
        Optional<Film> filmOptional = filmController.getFilmById(film.getId());

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(film);
    }
}