package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.NoWhiteSpace;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private long id;
    @NotNull(message = "Электронная почта не может быть null")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    private String email;
    @NoWhiteSpace(message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}