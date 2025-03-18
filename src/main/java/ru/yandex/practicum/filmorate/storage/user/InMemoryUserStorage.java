package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();
    private long countID = 0;

    @Override
    public User createUser(User user) {
        validationUser(user);
        user.setId(getNextID());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        if (updateUser.getId() == 0) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!users.containsKey(updateUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");
        }
        validationUser(updateUser);
        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return users.get(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private void validationUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private long getNextID() {
        return ++countID;
    }
}