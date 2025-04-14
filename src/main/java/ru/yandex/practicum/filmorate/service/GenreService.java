package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreStorage;

    public Genre getGenreById(int genreId) {
        return genreStorage.getGenreById(genreId);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }
}