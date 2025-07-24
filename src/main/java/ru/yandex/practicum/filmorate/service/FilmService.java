package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreStorage genreStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Optional<Film> getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Фильм с указанным id не найден."));

        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с указанным id не найден.");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Фильм с указанным id не найден."));
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}