package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    public final MpaStorage mpaStorage;

    public Mpa getMpaById(long mpaId) {
        return mpaStorage.getMpaById(mpaId);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}