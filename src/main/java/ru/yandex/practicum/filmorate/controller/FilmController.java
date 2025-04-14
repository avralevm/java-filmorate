package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        log.info("[POST] Создан фильм c ID: {}. Название: {}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updateFilm) {
        filmService.updateFilm(updateFilm);
        log.info("[PUT] Обновлены данные фильма c ID: {}. Название: {}", updateFilm.getId(), updateFilm.getName());
        return updateFilm;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("[GET] Запрос на получение фильма с id: {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("[GET] Запрос на получение всех фильмов");
        return filmService.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(userId, id);
        log.info("[PUT] Пользователь с ID: {} добавил лайк к фильму с ID: {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
       filmService.removeLike(userId, id);
        log.info("[DELETE] Пользователь с ID: {} удалил лайк к фильму с ID: {}", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("[GET] Запрос на получение самых популярных фильмов, количество: {}", count);
        return filmService.getMostPopularFilms(count);
    }
}