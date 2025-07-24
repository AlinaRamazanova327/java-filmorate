package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film) throws NotFoundException;

    List<Film> getFilms();

    Optional<Film> getFilmById(long filmId);

    void setFilmGenresIds(Long filmId, List<Integer> genresIds);

    void clearFilmGenresIds(Long filmId);

    void addFilmGenresIds(Long filmId, List<Integer> genresIds);

    List<Integer> getFilmGenresIds(Long filmId);

    List<Genre> getFilmGenres(Long filmId);

    List<Long> getFilmLikes(Long filmId);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}