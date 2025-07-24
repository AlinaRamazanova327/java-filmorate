/*package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@Deprecated
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    private long getNextId() {
        return films.values().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с указанным id не найден");
        }
        films.put(film.getId(), film);
        return film;
    }
}*/