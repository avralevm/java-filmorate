package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        log.info("[POST] Создан пользователь c ID: {}. Логин: {}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser) {
        userService.updateUser(updateUser);
        log.info("[PUT] Обновлены данные пользователя c ID: {}. Логин: {}", updateUser.getId(), updateUser.getLogin());
        return updateUser;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("[GET] Запрос на получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        log.info("[PUT] Пользователь с ID: {} добавил в друзья пользователя с ID: {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
        log.info("[DELETE] Пользователь с ID: {} удалил из друзья пользователя с ID: {}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("[GET] Запрос на получение всех друзей пользователя: {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("[GET] Получение списка общих друзей для пользователей с ID: {} и ID: {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}