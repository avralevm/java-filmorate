package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(Long userId, Long friendId) {
        checkIfExist(userId);
        checkIfExist(friendId);
        String sql = """
                INSERT INTO friendship (user_id, friend_id)
                VALUES (?, ?)
                """;
        jdbc.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        checkIfExist(userId);
        checkIfExist(friendId);
        String sql = """
                DELETE FROM friendship
                WHERE user_id = ? AND friend_id = ?
                """;
        jdbc.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        checkIfExist(userId);
        String sql = """
                SELECT u.*
                FROM friendship f
                JOIN users u ON f.friend_id = u.user_id
                WHERE f.user_id = ?
                """;
        List<User> users = jdbc.query(sql, new UserRowMapper(), userId);
        return users;
    }

    @Override
    public List<User> getMutualFriends(Long userId1, Long userId2) {
        checkIfExist(userId1);
        checkIfExist(userId2);
        String sql = """
                SELECT u.*
                FROM users AS u
                INNER JOIN friendship AS f1 ON u.user_id = f1.friend_id
                INNER JOIN friendship AS f2 ON u.user_id = f2.friend_id
                WHERE f1.user_id = ? AND f2.user_id = ?;
                """;
        List<User> friends = jdbc.query(sql, new UserRowMapper(), userId1, userId2);
        return friends;
    }

    private void checkIfExist(Long userId) {
        String sql = """
                SELECT *
                FROM users
                WHERE user_id = ?
                """;
        List<User> user = jdbc.query(sql, new UserRowMapper(), userId);
        if (user.size() != 1) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
    }
}