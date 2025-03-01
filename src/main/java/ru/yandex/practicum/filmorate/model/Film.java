package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @Past(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Длительность должна быть больше 0")
    private Integer duration;
}