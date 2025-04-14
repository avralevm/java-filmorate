package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbc;

    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        film.setMpa(mpa);

        String sqlGenre = """
                    SELECT g.*
                    FROM genres AS g
                    JOIN film_genre AS fg ON g.genre_id = fg.genre_id
                    WHERE fg.film_id = ?
                    ORDER BY g.genre_id
                """;
        List<Genre> genres = jdbc.query(sqlGenre, new GenreRowMapper(), film.getId());
        film.setGenres(new HashSet<>(genres));

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbc.queryForList(likesSql, Long.class, film.getId());
        film.setLikes(new HashSet<>(likes));
        return film;
    }
}