package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public Film createFilm(Film film) {
        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId());
        film.setMpa(mpa);
        List<Genre> genres = genreStorage.getFilmGenres(film);
        film.setGenres(Set.copyOf(genres));
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film updateFilm) {
        Mpa mpa = mpaStorage.getMpaById(updateFilm.getMpa().getId());
        updateFilm.setMpa(mpa);
        List<Genre> genres = genreStorage.getFilmGenres(updateFilm);
        updateFilm.setGenres(Set.copyOf(genres));
        return filmStorage.updateFilm(updateFilm);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addLike(Long userId, Long filmId) {
        validation(userId, filmId);
        likeStorage.addLike(userId, filmId);
    }

    public void removeLike(Long userId, Long filmId) {
        validation(userId, filmId);
        likeStorage.removeLike(userId, filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getPopularFilmByLike(count);
    }

    private void validation(Long userId, Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Не указан id фильма");
        }
        if (userId == null) {
            throw new ValidationException("Не указан id пользователя");
        }
    }
}