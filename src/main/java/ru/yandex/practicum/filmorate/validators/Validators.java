package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.model.User;

public class Validators {
    public static void validationUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}