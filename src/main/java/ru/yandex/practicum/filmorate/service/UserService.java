package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updateUser) {
        return userStorage.updateUser(updateUser);
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        validation(userId, friendId);
        friendshipDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validation(userId, friendId);
        friendshipDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        return friendshipDbStorage.getFriends(userId);
    }

    public List<User> getMutualFriends(Long userId, Long userId2) {
        return friendshipDbStorage.getMutualFriends(userId, userId2);
    }

    private void validation(Long userId, Long friendId) {
        if (friendId == null) {
            throw new ValidationException("Не указан id друга");
        }
        if (userId == null) {
            throw new ValidationException("Не указан id пользователя");
        }
        if (userId == friendId) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }
    }
}