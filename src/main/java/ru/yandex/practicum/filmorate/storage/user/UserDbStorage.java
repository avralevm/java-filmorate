package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;

    @Override
    public User createUser(User user) {
        String sql = """
                INSERT INTO users (login, name, email, birthday)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        String sql = """
                UPDATE users
                SET login = ?, name = ?, email = ?, birthday = ?
                WHERE user_id = ?
                """;
        int rowsUpdated = jdbc.update(
                sql,
                updateUser.getLogin(),
                updateUser.getName(),
                updateUser.getEmail(),
                updateUser.getBirthday(),
                updateUser.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");
        }

        return updateUser;
    }

    @Override
    public User getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        List<User> user = jdbc.query(sql, new UserRowMapper(), userId);
        if (user.size() != 1) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        return user.get(0);
    }

    @Override
    public List<User> getUsers() {
        String sql = """
                SELECT *
                FROM users
                """;
        List<User> users = jdbc.query(sql, new UserRowMapper());
        return users;
    }
}