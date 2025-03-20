package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> films = new HashMap<>();
    private long countID = 0;

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextID());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film updateFilm) {
        if (updateFilm.getId() == 0) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!films.containsKey(updateFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
        }
        films.put(updateFilm.getId(), updateFilm);
        return updateFilm;
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Пользователь с id = " + filmId + " не найден");
        }
        return films.get(filmId);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private long getNextID() {
        return ++countID;
    }
}