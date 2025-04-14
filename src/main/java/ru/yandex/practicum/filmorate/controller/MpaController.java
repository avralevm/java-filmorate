package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    public final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable long id) {
        log.info("[GET] Запрос на получение рейтинга ID: {}", id);
        return mpaService.getMpaById(id);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("[GET] Запрос на получение всех рейтингов");
        return mpaService.getAllMpa();
    }
}