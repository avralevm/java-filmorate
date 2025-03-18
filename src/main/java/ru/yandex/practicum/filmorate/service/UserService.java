package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updateUser) {
        return userStorage.updateUser(updateUser);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        validation(userId, friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validation(userId, friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getFriends(Long userId) {
        Set<Long> friendsId = userStorage.getUserById(userId).getFriends();
        return userStorage.getUsers().stream()
                .filter(user -> friendsId.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long userId, Long otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);
        Set<Long> friends = new HashSet<>(user.getFriends());
        friends.retainAll(otherUser.getFriends());

        return userStorage.getUsers().stream()
                .filter(mutualFriend -> friends.contains(mutualFriend.getId()))
                .collect(Collectors.toList());
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