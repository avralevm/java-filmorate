package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = DEFINED_PORT)
class UserRestControllerTest {
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private UserController userController;

	@Test
	public void contextLoads() {
		assertThat(userController).isNotNull();
	}

	private User createUser(String login, String email, String name, LocalDate birthday) {
		return User.builder()
				.login(login)
				.email(email)
				.name(name)
				.birthday(birthday)
				.build();
	}

	@Test
	public void creteUserTest() {
		User user = createUser("User1", "userMail@mail.ru", "User Nike Name",
				LocalDate.of(1946, 8, 20));
		ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
		assertThat(response.getStatusCode()).isEqualTo(OK);

		User createdUser = response.getBody();
		assertNotNull(createdUser);
		assertEquals(user.getName(), createdUser.getName(), "User создался с некорректным именем");
		assertEquals(user.getEmail(), createdUser.getEmail(), "User создался с некорректным email");
		assertEquals(user.getLogin(), createdUser.getLogin(),"User создался с некорректным login");
		assertEquals(user.getBirthday(), createdUser.getBirthday(), "User создался с некорректным birthday");
	}

	@Test
	public void updateUserTest() {
		User user = createUser("User1", "userMail@mail.ru", "User Nike Name", LocalDate.of(1946, 8, 20));
		User userUpdate = createUser("UserUpdate", "mailUpdate@mail.ru", "Nick Name Update", LocalDate.of(1946, 8, 20));
		userController.createUser(user);
		userUpdate.setId(user.getId());

		ResponseEntity<User> response = restTemplate.exchange("/users", HttpMethod.PUT, new HttpEntity<>(userUpdate), User.class);
		assertThat(response.getStatusCode()).isEqualTo(OK);

		User updatedUser  = response.getBody();
		assertNotNull(updatedUser);
		assertEquals(userUpdate.getId(), updatedUser.getId(), "User создался с некорректным именем");
		assertEquals(userUpdate.getName(), updatedUser.getName(), "User создался с некорректным именем");
		assertEquals(userUpdate.getEmail(), updatedUser.getEmail(), "User создался с некорректным email");
		assertEquals(userUpdate.getLogin(), updatedUser.getLogin(),"User создался с некорректным login");
	}

	@Test
	public void getUsersTest() {
		ResponseEntity<List<User>> response = restTemplate.exchange("/users",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<User>>() {});
		assertThat(response.getStatusCode()).isEqualTo(OK);

		List<User> users = response.getBody();
		assertNotNull(users);
	}

	@Test
	public void addFriendTest() {
		User user = createUser("User1", "userMail@mail.ru", "User Nike Name", LocalDate.of(1946, 8, 20));
		User friend = createUser("Friend", "friend@mail.ru", "Friend", LocalDate.of(2000, 8, 20));
		userController.createUser(user);
		userController.createUser(friend);

		ResponseEntity<Void> response = restTemplate.exchange(
				"/users//{id}/friends/{friendId}", HttpMethod.PUT,
				null,
				Void.class,
				user.getId(),
				friend.getId()
		);

		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	public void removeFriendTest() {
		User user = createUser("User1", "userMail@mail.ru", "User Nike Name", LocalDate.of(1946, 8, 20));
		User friend = createUser("Friend", "friend@mail.ru", "Friend", LocalDate.of(2000, 8, 20));
		userController.createUser(user);
		userController.createUser(friend);

		userController.addFriend(user.getId(), friend.getId());

		ResponseEntity<Void> response = restTemplate.exchange("/users//{id}/friends/{friendId}",
				HttpMethod.DELETE,
				null,
				Void.class,
				user.getId(),
				friend.getId()
		);

		assertThat(response.getStatusCode()).isEqualTo(OK);
	}

	@Test
	public void getUserFriendsTest() {
		User user = createUser("User1", "userMail@mail.ru", "User Nike Name", LocalDate.of(1946, 8, 20));
		User friend = createUser("Friend", "friend@mail.ru", "Friend", LocalDate.of(2000, 8, 20));
		userController.createUser(user);
		userController.createUser(friend);
		userController.addFriend(user.getId(), friend.getId());

		ResponseEntity<List<User>> response = restTemplate.exchange(
				"/users/{id}/friends",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<User>>() {},
				user.getId()
		);

		assertThat(response.getStatusCode()).isEqualTo(OK);

		List<User> userFriends = response.getBody();
		assertNotNull(userFriends);
		assertEquals(1, userFriends.size());
	}

	@Test
	public void getMutualFriendsTest() {
		User user = createUser("User1", "userMail@mail.ru", "User Nike Name", LocalDate.of(1946, 8, 20));
		User friend = createUser("Friend", "friend@mail.ru", "Friend", LocalDate.of(2000, 8, 20));
		userController.createUser(user);
		userController.createUser(friend);
		userController.addFriend(user.getId(), friend.getId());

		ResponseEntity<List<Film>> response = restTemplate.exchange(
				"/users/{id}/friends/common/{otherId}",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<Film>>() {},
				user.getId(),
				friend.getId()
		);

		assertThat(response.getStatusCode()).isEqualTo(OK);

		List<Film> mostPopularFilms = response.getBody();
		assertNotNull(mostPopularFilms);
	}

	@Test
	public void validationLoginTest_EmptyLogin() {
		User userWithoutLogin = createUser(null, "mail@mail.ru", "Nick Name", LocalDate.of(1946, 8, 20));
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userWithoutLogin, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody).contains("\"login\":\"Логин не может быть пустым и содержать пробелы\"");
		assertThat(responseBody).contains("\"error\":\"Validation Error\"");
		assertThat(responseBody).contains("\"status\":400");
		assertThat(responseBody).contains("\"timestamp\":");
	}

	@Test
	public void validateEmailTest_EmptyEmail() {
		User userWithoutEmail = createUser("dolore", null, "Nick Name", LocalDate.of(1946, 8, 20));
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userWithoutEmail, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody).contains("\"email\":\"Электронная почта не может быть null\"");
		assertThat(responseBody).contains("\"error\":\"Validation Error\"");
		assertThat(responseBody).contains("\"status\":400");
		assertThat(responseBody).contains("\"timestamp\":");
	}

	@Test
	public void validateEmailTest_EmailWithoutAt() {
		User userEmail = createUser("dolore", "mail$mail.ru", "Nick Name", LocalDate.of(1946, 8, 20));
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userEmail, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody).contains("\"email\":\"Электронная почта не может быть пустой и должна содержать символ @\"");
		assertThat(responseBody).contains("\"error\":\"Validation Error\"");
		assertThat(responseBody).contains("\"status\":400");
		assertThat(responseBody).contains("\"timestamp\":");
	}

	@Test
	public void validateEmailTest_BirthdayInFuture() {
		User userBirthdayInFuture = createUser("dolore", "mail@mail.ru", "Nick Name", LocalDate.of(2030, 8, 20));
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userBirthdayInFuture, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertThat(responseBody).isNotNull();
		assertThat(responseBody).contains("\"birthday\":\"Дата рождения не может быть в будущем\"");
		assertThat(responseBody).contains("\"error\":\"Validation Error\"");
		assertThat(responseBody).contains("\"status\":400");
		assertThat(responseBody).contains("\"timestamp\":");
	}
}