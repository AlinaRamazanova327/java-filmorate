package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private Long filmCounter = 1L;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
            film.setId(filmCounter++);
            films.put(film.getId(), film);
            log.info("Фильм добавлен: {}", film);
            return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws NotFoundException {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм с указанным id не найден");
        }
        Film existingFilm = films.get(film.getId());
        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        log.info("Фильм обновлен: {}", existingFilm);
        return existingFilm;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }
}