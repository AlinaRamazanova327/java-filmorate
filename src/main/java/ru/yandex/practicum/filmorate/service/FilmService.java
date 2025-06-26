package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getFilms() {
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
        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId).orElseThrow(() ->
                new NotFoundException("Фильм с указанным id не найден."));
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Пользователь с указанным id не найден.");
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (getFilms().isEmpty()) {
            return Collections.emptyList();
        }
        return getFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}