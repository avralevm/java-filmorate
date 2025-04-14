package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Mpa getMpaById(long mpaId) {
        String sql = """
                SELECT *
                FROM mpa
                WHERE mpa_id = ?
                """;
        List<Mpa> mpa = jdbc.query(sql, new MpaRowMapper(), mpaId);
        if (mpa.size() != 1) {
            throw new NotFoundException("Рейтинг с id: " + mpaId + " не найден");
        }
        return mpa.get(0);
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = """
                SELECT *
                FROM mpa
                ORDER BY mpa_id
                """;
        List<Mpa> allMpa = jdbc.query(sql, new MpaRowMapper());
        return allMpa;
    }
}