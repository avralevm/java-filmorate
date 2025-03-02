package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static ru.yandex.practicum.filmorate.validators.Validators.validationUser;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();
    private long countID = 0;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validationUser(user);
        user.setId(getNextID());
        users.put(user.getId(), user);
        log.info("[POST] Создан пользователь c ID: {}. Логин: {}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser) {
        if (updateUser.getId() == 0) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!users.containsKey(updateUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");
        }
        validationUser(updateUser);
        users.put(updateUser.getId(), updateUser);
        log.info("[PUT] Обновлены данные пользователя c ID: {}. Логин: {}", updateUser.getId(), updateUser.getLogin());
        return updateUser;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private long getNextID() {
        return ++countID;
    }
}