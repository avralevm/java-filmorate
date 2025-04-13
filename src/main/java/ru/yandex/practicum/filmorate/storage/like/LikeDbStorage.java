package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbc;

    @Override
    public void addLike(Long userId, Long filmId) {
        checkIfExist(userId, filmId);
        String sql = """
                INSERT INTO likes (user_id, film_id)
                VALUES (?, ?)
                """;
        jdbc.update(sql, userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        checkIfExist(userId, filmId);
        String sql = """
                DELETE FROM likes
                WHERE user_id = ? AND film_id = ?
                """;
        jdbc.update(sql, userId, filmId);
    }

    private void checkIfExist(Long userId, Long filmId) {
        String userSql = "SELECT * FROM users WHERE user_id = ?";
        List<User> user = jdbc.query(userSql, new UserRowMapper(), userId);
        if (user.size() != 1) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }

        String filmSql = """
                    SELECT f.*, m.*
                    FROM films AS f
                    LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
                    WHERE f.film_id = ?
                """;
        List<Film> film = jdbc.query(filmSql, new FilmRowMapper(jdbc), filmId);
        if (user.size() != 1) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден");
        }
    }
}