package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;

        filmRowMapper =
                ((rs, rowNum) -> Film.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .duration(rs.getInt("duration"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .mpa(mpaService.getMpaById(rs.getInt("mpa_rating")))
                        .genres(getFilmGenres(rs.getLong("id")))
                        .genresIds(getFilmGenresIds(rs.getLong("id")))
                        .likes(getFilmLikes(rs.getLong("id")))
                        .build());
    }

    @Override
    public Film createFilm(Film film) {
        validateMpaAndGenres(film);
        validateLikes(film);

        String sql = """
        INSERT INTO films (name, description, duration, release_date, mpa_rating)
        VALUES (?, ?, ?, ?, ?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKeyAs(Long.class));
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        if (film.getLikes() != null) {
            for (Long userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }
        addFilmGenres(film.getId(), film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()).stream().toList());
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
        validateMpaAndGenres(film);
        validateLikes(film);

        String sql = """
                UPDATE films
                SET name = ?, description = ?, duration = ?, release_date = ?, mpa_rating = ?
                WHERE id = ?""";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId(),
                film.getId());

        clearFilmLikes(film.getId());
        if (film.getLikes() != null) {
            for (Long userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }
        setFilmGenresIds(film.getId(), film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()).stream().toList());
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, filmRowMapper, filmId));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public void addFilmGenresIds(Long filmId, List<Integer> genresIds) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, genresIds.stream()
                .map(genreId -> new Object[]{filmId, genreId})
                .toList());
    }

    @Override
    public void setFilmGenresIds(Long filmId, List<Integer> genresIds) {
        clearFilmGenresIds(filmId);
        addFilmGenresIds(filmId, genresIds);
    }

    @Override
    public void clearFilmGenresIds(Long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Integer> getFilmGenresIds(Long filmId) {
        String sql = """
                SELECT genre_id
                FROM film_genres
                WHERE film_id = ?""";
        return jdbcTemplate.queryForList(sql, Integer.class, filmId);
    }

    private void addFilmGenres(Long filmId, List<Integer> genresIds) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Integer genreId : genresIds) {
            jdbcTemplate.update(sql, filmId, genreId);
        }
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        String sql = """
                SELECT g.*
                FROM film_genres fg
                JOIN genres g ON fg.genre_id = g.id
                WHERE fg.film_id = ?
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, rs -> {
            List<Genre> results = new ArrayList<>();
            while (rs.next()) {
                results.add(Genre.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("genre"))
                        .build());
            }
            return results;
        }, filmId);
    }

    private void validateMpaAndGenres(Film film) {
        if (mpaService.getMpaById(film.getMpa().getId()) == null) {
            throw new NotFoundException("значение не найдено");
        }
        for (Genre genre : film.getGenres()) {
            if (genreService.getGenreById(genre.getId()) == null) {
                throw new NotFoundException("Жанр не найден");
            }
        }
    }

    @Override
    public List<Long> getFilmLikes(Long filmId) {
        String sql = """
                    SELECT user_id
                    FROM likes
                    WHERE film_id = ?
                """;
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private void validateLikes(Film film) throws NotFoundException {
        if (film.getLikes() != null) {
            for (Long userId : film.getLikes()) {
                if (userId == null) {
                    throw new NotFoundException("ID пользователя в списке лайков не может быть null");
                }
            }
        }
    }

    private void clearFilmLikes(Long filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
                    SELECT f.*
                    FROM films f
                    JOIN likes l ON f.id = l.film_id
                    GROUP BY f.id
                    ORDER BY COUNT(l.user_id) DESC
                    LIMIT ?
                """;
        return jdbcTemplate.query(sql, filmRowMapper, count);
    }
}