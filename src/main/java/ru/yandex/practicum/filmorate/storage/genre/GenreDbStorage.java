package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedParameter;

    @Override
    public Genre getGenreById(int genreId) {
        String sql = """
                SELECT *
                FROM genres
                WHERE genre_id = ?
                """;
        List<Genre> genre = jdbc.query(sql, new GenreRowMapper(), genreId);
        if (genre.size() != 1) {
            throw new NotFoundException("Жанр с id: " + genreId + " не найден");
        }
        return genre.get(0);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = """
                SELECT *
                FROM genres
                ORDER BY genre_id
                """;
        List<Genre> genres = jdbc.query(sql, new GenreRowMapper());
        return genres;
    }

    @Override
    public List<Genre> getFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        String sql = """
                SELECT *
                FROM genres
                WHERE genre_id IN(:genreIds)
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("genreIds", genreIds);

        List<Genre> genres = namedParameter.query(sql, params, new GenreRowMapper());

        if (genres.isEmpty()) {
            throw new NotFoundException("Жанры с id " + genreIds + " не найдены");
        }
        return genres;
    }
}