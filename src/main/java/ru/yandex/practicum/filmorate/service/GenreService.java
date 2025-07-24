package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre getGenreById(Integer id) {
        return genreStorage.getById(id).orElseThrow(() -> new NotFoundException("Жанр с указанным id не найден"));
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }
}