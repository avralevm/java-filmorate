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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = DEFINED_PORT)
class UserControllerTest {
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private UserController userController;

	@Test
	public void contextLoads() {
		assertThat(userController).isNotNull();
	}

	@Test
	public void creteUserTest() {
		User user = User.builder()
				.login("dolore")
				.email("mail@mail.ru")
				.name("Nick Name")
				.birthday(LocalDate.of(1946,8,20)).build();
		ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);
		assertThat(response.getStatusCode()).isEqualTo(OK);

		User createdUser = response.getBody();
		assertNotNull(createdUser);
		assertEquals(user.getName(), createdUser.getName(), "User создался с некорректным именем");
		assertEquals(user.getEmail(), createdUser.getEmail(), "User создался с некорректным email");
		assertEquals(user.getLogin(), createdUser.getLogin(),"User создался с некорректным login");
		assertEquals(user.getBirthday(), createdUser.getBirthday(), "User создался с некорректным birthday");
		assertEquals(1, userController.getUsers().size(), "User не создался");
	}

	@Test
	public void updateUserTest() {
		User userUpdate = User.builder()
				.id(1)
				.login("doloreUpdate")
				.email("mailUpdate@mail.ru")
				.name("Nick Name Update")
				.birthday(LocalDate.of(1946,8,20)).build();
		ResponseEntity<User> response = restTemplate.exchange("/users", HttpMethod.PUT, new HttpEntity<>(userUpdate), User.class);
		assertThat(response.getStatusCode()).isEqualTo(OK);

		User updatedUser  = response.getBody();
		assertNotNull(updatedUser);
		assertEquals(userUpdate.getId(), updatedUser.getId(), "User создался с некорректным именем");
		assertEquals(userUpdate.getName(), updatedUser.getName(), "User создался с некорректным именем");
		assertEquals(userUpdate.getEmail(), updatedUser.getEmail(), "User создался с некорректным email");
		assertEquals(userUpdate.getLogin(), updatedUser.getLogin(),"User создался с некорректным login");
		assertEquals(1, userController.getUsers().size(), "Неправильное количество User после обновления");
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
		assertEquals(1, userController.getUsers().size(), "Неправильное количество User");
	}

	@Test
	public void validationLoginTest_EmptyLogin() {
		User userWithoutLogin = User.builder()
				.email("mail@mail.ru")
				.name("Nick Name")
				.birthday(LocalDate.of(1946, 8, 20)).build();
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userWithoutLogin, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertNotNull(responseBody);
		assertTrue(responseBody.contains("\"login\":\"Логин не может быть пустым\""));
		assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
		assertTrue(responseBody.contains("\"status\":400"));
		assertTrue(responseBody.contains("\"timestamp\":"));
	}

	@Test
	public void validationLoginTest_LoginWithSpaces() {
		User userLoginSpace = User.builder()
				.login("dolore Space")
				.email("mail@mail.ru")
				.name("Nick Name")
				.birthday(LocalDate.of(1946, 8, 20)).build();
		ResponseEntity<String> responseErrorSpace = restTemplate.postForEntity("/users", userLoginSpace, String.class);
		assertThat(responseErrorSpace.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseErrorSpace.getBody();
		assertNotNull(responseBody);
		System.out.println(responseBody);
		assertTrue(responseBody.contains("\"login\":\"Логин не может содержать пробелы\""));
		assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
		assertTrue(responseBody.contains("\"status\":400"));
		assertTrue(responseBody.contains("\"timestamp\":"));
	}

	@Test
	public void validateEmailTest_EmptyEmail() {
		User userWithoutEmail = User.builder()
				.login("dolore")
				.name("Nick Name")
				.birthday(LocalDate.of(1946, 8, 20)).build();
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userWithoutEmail, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertNotNull(responseBody);
		assertTrue(responseBody.contains("\"email\":\"Электронная почта не может быть null\""));
		assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
		assertTrue(responseBody.contains("\"status\":400"));
		assertTrue(responseBody.contains("\"timestamp\":"));
	}

	@Test
	public void validateEmailTest_EmailWithSpaces() {
		User userEmailSpace = User.builder()
				.email("   ")
				.login("dolore")
				.name("Nick Name")
				.birthday(LocalDate.of(1946, 8, 20)).build();
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userEmailSpace, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertNotNull(responseBody);
		assertTrue(responseBody.contains("\"email\":\"Электронная почта не может быть пустой и должна содержать символ @\""));
		assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
		assertTrue(responseBody.contains("\"status\":400"));
		assertTrue(responseBody.contains("\"timestamp\":"));
	}

	@Test
	public void validateEmailTest_EmailWithoutAt() {
		User userEmail = User.builder()
				.email("mail$mail.ru")
				.login("dolore")
				.name("Nick Name")
				.birthday(LocalDate.of(1946, 8, 20)).build();
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userEmail, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertNotNull(responseBody);
		assertTrue(responseBody.contains("\"email\":\"Электронная почта не может быть пустой и должна содержать символ @\""));
		assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
		assertTrue(responseBody.contains("\"status\":400"));
		assertTrue(responseBody.contains("\"timestamp\":"));
	}

	@Test
	public void validateEmailTest_BirthdayInFuture() {
		User userBirthdayInFuture = User.builder()
				.email("mail@mail.ru")
				.login("dolore")
				.name("Nick Name")
				.birthday(LocalDate.of(2030, 8, 20)).build();
		ResponseEntity<String> responseError = restTemplate.postForEntity("/users", userBirthdayInFuture, String.class);
		assertThat(responseError.getStatusCode()).isEqualTo(BAD_REQUEST);

		String responseBody = responseError.getBody();
		assertNotNull(responseBody);
		assertTrue(responseBody.contains("\"birthday\":\"Дата рождения не может быть в будущем\""));
		assertTrue(responseBody.contains("\"error\":\"Validation Error\""));
		assertTrue(responseBody.contains("\"status\":400"));
		assertTrue(responseBody.contains("\"timestamp\":"));
	}
}