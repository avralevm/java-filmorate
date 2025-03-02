package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Long, Film> films = new HashMap<>();
    private long countID = 0;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextID());
        films.put(film.getId(), film);
        log.info("[POST] Создан фильм c ID: {}. Название: {}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updateFilm) {
        if (updateFilm.getId() == 0) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!films.containsKey(updateFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
        }

        log.info("[PUT] Обновлены данные фильма c ID: {}. Название: {}", updateFilm.getId(), updateFilm.getName());
        films.put(updateFilm.getId(), updateFilm);
        return updateFilm;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private long getNextID() {
        return ++countID;
    }
}