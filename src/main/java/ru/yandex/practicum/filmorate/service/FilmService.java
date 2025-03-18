package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }


    public Film updateFilm(Film updateFilm) {
        return filmStorage.updateFilm(updateFilm);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addLike(Long userId, Long filmId) {
        validation(userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        film.addLike(userId);
    }

    public void removeLike(Long userId, Long filmId) {
        validation(userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        film.removeLike(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getFilms());
        return films.stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validation(Long userId, Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Не указан id фильма");
        }
        if (userId == null) {
            throw new ValidationException("Не указан id пользователя");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}