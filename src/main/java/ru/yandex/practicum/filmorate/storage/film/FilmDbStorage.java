package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Film createFilm(Film film) {
        String sql = """
                INSERT INTO films (name, description, release_date, duration, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

       addFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film updateFilm) {
        String sql = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                WHERE film_id = ?
                """;
        int rowsUpdated = jdbc.update(sql,
                updateFilm.getName(),
                updateFilm.getDescription(),
                updateFilm.getReleaseDate(),
                updateFilm.getDuration(),
                updateFilm.getMpa().getId(),
                updateFilm.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Фильм с id = " + updateFilm.getId() + " не найден");
        }

        addFilmGenres(updateFilm);
        return updateFilm;
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sql = """
                    SELECT f.*, m.*
                    FROM films AS f
                    LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
                    WHERE f.film_id = ?
                """;
        List<Film> films = jdbc.query(sql, new FilmRowMapper(jdbc), filmId);
        if (films.size() != 1) {
            throw new NotFoundException("Фильм с id: " + filmId + " не найден");
        }
        return films.get(0);
    }

    @Override
    public List<Film> getFilms() {
        String sql = """
                    SELECT f.*, m.*
                    FROM films AS f
                    LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
                    ORDER BY f.film_id
                """;
        List<Film> films = jdbc.query(sql, new FilmRowMapper(jdbc));
        return films;
    }

    @Override
    public List<Film> getPopularFilmByLike(int count) {
        String sql = """
                SELECT f.*, m.mpa_id, m.mpa_name, COUNT(l.user_id) AS likes_count
                FROM films f
                LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
                LEFT JOIN likes l ON f.film_id = l.film_id
                GROUP BY f.film_id, m.mpa_id, m.mpa_name
                ORDER BY likes_count DESC
                LIMIT ?
                """;
        List<Film> films = jdbc.query(sql, new FilmRowMapper(jdbc), count);
        return films;
    }

    private void addFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        Set<Genre> genres = film.getGenres();
        String genreSql = """
                INSERT INTO film_genre (film_id, genre_id)
                VALUES (?, ?)
                """;
        jdbc.batchUpdate(genreSql, genres, genres.size(), (ps, genre) -> {
            ps.setLong(1, film.getId());
            ps.setInt(2, genre.getId());
        });
        film.setGenres(genres);
    }
}