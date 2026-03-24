package ch.uzh.ifi.hase.soprafs26.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		testUser = new User();
		testUser.setId(1L);
		testUser.setEmail("test@example.com");
		testUser.setUsername("testUsername");
		testUser.setPassword("secret");

		Mockito.when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
		Mockito.when(userRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
	}

	@Test
	public void createUser_validInputs_success() {
		User createdUser = userService.createUser(testUser);

		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
		assertEquals(testUser.getEmail(), createdUser.getEmail());
		assertEquals(testUser.getUsername(), createdUser.getUsername());
		assertEquals("hashed-secret", createdUser.getPassword());
		assertNotNull(createdUser.getToken());
		assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
	}

	@Test
	public void createUser_duplicateEmail_throwsException() {
		userService.createUser(testUser);
		Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
		Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

		assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
	}

	@Test
	public void createUser_duplicateInputs_throwsException() {
		userService.createUser(testUser);
		Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
		Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

		assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
	}

	@Test
	public void loginUser_validCredentials_success() {
		User persistedUser = new User();
		persistedUser.setUsername("testUsername");
		persistedUser.setPassword("hashed-secret");
		persistedUser.setStatus(UserStatus.OFFLINE);
		persistedUser.setToken("old-token");

		Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(persistedUser);
		Mockito.when(passwordEncoder.matches("secret", "hashed-secret")).thenReturn(true);
		Mockito.when(userRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

		User loggedInUser = userService.loginUser("testUsername", "secret");

		assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
		assertNotNull(loggedInUser.getToken());
		assertNotEquals("old-token", loggedInUser.getToken());
	}

	@Test
	public void loginUser_invalidPassword_throwsUnauthorized() {
		User persistedUser = new User();
		persistedUser.setUsername("testUsername");
		persistedUser.setPassword("hashed-secret");

		Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(persistedUser);
		Mockito.when(passwordEncoder.matches("wrong", "hashed-secret")).thenReturn(false);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> userService.loginUser("testUsername", "wrong"));
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
	}
}
