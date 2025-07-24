package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper =
            ((rs, rowNum) -> User.builder()
                    .id(rs.getLong("id"))
                    .login(rs.getString("login"))
                    .email(rs.getString("email"))
                    .name(rs.getString("name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .build());

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKeyAs(Long.class));
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = """
                    SELECT u.*
                    FROM friends f
                    JOIN users u ON f.friend_id = u.id
                    WHERE f.user_id = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        String sql = """
                    SELECT u.*
                    FROM friends f1
                    JOIN friends f2 ON f1.friend_id = f2.friend_id
                    JOIN users u ON f1.friend_id = u.id
                    WHERE f1.user_id = ? AND f2.user_id = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }
}