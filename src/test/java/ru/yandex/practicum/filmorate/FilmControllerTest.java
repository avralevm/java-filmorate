package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = DEFINED_PORT)
public class FilmControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private FilmController filmController;

    @Test
    public void contextLoads() {
        assertThat(filmController).isNotNull();
    }

    @Test
    public void creteUserTest() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1967,3,25))
                .duration(100)
                .build();
        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        Film createdFilm = response.getBody();
        assertNotNull(createdFilm);
        assertEquals(film.getName(), createdFilm.getName(), "Film создался с некорректным именем");
        assertEquals(film.getDescription(), createdFilm.getDescription(), "Film создался с некорректным описанием");
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate(),"Film создался с некорректным releaseDate");
        assertEquals(film.getDuration(), createdFilm.getDuration(), "Film создался с некорректным duration");
        assertEquals(1, filmController.getFilms().size(), "User не создался");
    }

    @Test
    public void updateFilmTest() {
        Film filmUpdate = Film.builder()
                .id(1)
                .name("Name Update")
                .description("Description Update")
                .releaseDate(LocalDate.of(1967,3,25))
                .duration(150)
                .build();
        ResponseEntity<Film> response = restTemplate.exchange("/films", HttpMethod.PUT, new HttpEntity<>(filmUpdate), Film.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);

        Film updatedFilm = response.getBody();
        assertNotNull(updatedFilm);
        assertEquals(filmUpdate.getName(), updatedFilm.getName(), "Film создался с некорректным именем");
        assertEquals(filmUpdate.getDescription(), updatedFilm.getDescription(), "Film создался с некорректным описанием");
        assertEquals(filmUpdate.getReleaseDate(), updatedFilm.getReleaseDate(),"Film создался с некорректным releaseDate");
        assertEquals(filmUpdate.getDuration(), updatedFilm.getDuration(), "Film создался с некорректным duration");
        assertEquals(1, filmController.getFilms().size(), "User не создался");
    }

    @Test
    public void getFilmsTest() {
        ResponseEntity<List<Film>> response = restTemplate.exchange("/films",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Film>>() {});
        assertThat(response.getStatusCode()).isEqualTo(OK);

        List<Film> films = response.getBody();
        assertNotNull(films);
        assertEquals(1, filmController.getFilms().size(), "Неправильное количество User");
    }

    @Test
    public void validationNameTest_EmptyName() {
        Film filmUpdate = Film.builder()
                .description("Description")
                .releaseDate(LocalDate.of(1967,3,25))
                .duration(100)
                .build();
        ResponseEntity<String> responseError = restTemplate.postForEntity("/films", filmUpdate, String.class);
        assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

        String responseBody = responseError.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("\"name\":\"Название фильма не может быть пустым\""));
        assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
        assertTrue(responseBody.contains("\"status\":400"));
        assertTrue(responseBody.contains("\"timestamp\":"));
    }

    @Test
    public void validationDescriptionTest_DescriptionLongerMaxSize() {
        Film film = Film.builder()
                .name("Name")
                .description("Пятеро друзей (комик-группа «Шарло»), приезжают в город Бризуль." +
                        " Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов." +
                        " о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        ResponseEntity<String> responseError = restTemplate.postForEntity("/films", film, String.class);
        assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

        String responseBody = responseError.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("\"description\":\"Максимальная длина описания — 200 символов\""));
        assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
        assertTrue(responseBody.contains("\"status\":400"));
        assertTrue(responseBody.contains("\"timestamp\":"));
    }

    @Test
    public void validationReleaseDateTest_ReleaseDateNotBeforeCinemaBirthday() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1800, 3, 25))
                .duration(100)
                .build();
        ResponseEntity<String> responseError = restTemplate.postForEntity("/films", film, String.class);
        assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

        String responseBody = responseError.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("\"releaseDate\":\"Дата релиза не должна быть раньше 1895-12-28\""));
        assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
        assertTrue(responseBody.contains("\"status\":400"));
        assertTrue(responseBody.contains("\"timestamp\":"));
    }

    @Test
    public void validationDuration_DurationMostBePositiveNumber() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1800, 3, 25))
                .duration(-1)
                .build();
        ResponseEntity<String> responseError = restTemplate.postForEntity("/films", film, String.class);
        assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

        String responseBody = responseError.getBody();
        assertNotNull(responseBody);

        assertTrue(responseBody.contains("\"duration\":\"Длительность должна быть больше 0\""));
        assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
        assertTrue(responseBody.contains("\"status\":400"));
        assertTrue(responseBody.contains("\"timestamp\":"));
    }
}