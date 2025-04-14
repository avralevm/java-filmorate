package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film createFilm(Film film);

    public Film updateFilm(Film updateFilm);

    public Film getFilmById(Long filmId);

    public List<Film> getFilms();

    List<Film> getPopularFilmByLike(int count);
}