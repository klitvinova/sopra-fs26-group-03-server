package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.Unit;
import ch.uzh.ifi.hase.soprafs26.entity.Ingredient;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngredientServiceTest {

	@Mock
	private IngredientRepository ingredientRepository;

	@InjectMocks
	private IngredientService ingredientService;

	private Ingredient testIngredient;
	private User testUser;

	@BeforeEach
    void setup() {
		MockitoAnnotations.openMocks(this);

		testIngredient = new Ingredient();
		testIngredient.setIngredientName("Milk");
		testIngredient.setUnit(Unit.LITER);

		testUser = new User();
		testUser.setUserID("user-1");
		testIngredient.setUser(testUser);
	}

	@Test
    void createIngredient_validInput_success() {
		when(ingredientRepository.findByIngredientNameIgnoreCaseAndUser("Milk", testUser)).thenReturn(Optional.empty());
		when(ingredientRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Ingredient created = ingredientService.createIngredient(testIngredient);

		assertNotNull(created);
		assertEquals("Milk", created.getIngredientName());
		verify(ingredientRepository, times(1)).saveAndFlush(any());
	}

	@Test
    void createIngredient_duplicateName_throwsConflict() {
		when(ingredientRepository.findByIngredientNameIgnoreCaseAndUser("Milk", testUser))
				.thenReturn(Optional.of(testIngredient));

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> ingredientService.createIngredient(testIngredient));
		assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
	}

	@Test
    void createIngredient_missingName_throwsBadRequest() {
		testIngredient.setIngredientName(null);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> ingredientService.createIngredient(testIngredient));
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}

	@Test
    void createIngredient_missingUnit_throwsBadRequest() {
		testIngredient.setUnit(null);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> ingredientService.createIngredient(testIngredient));
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}

	@Test
    void createIngredient_missingUser_throwsUnauthorized() {
		testIngredient.setUser(null);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> ingredientService.createIngredient(testIngredient));
		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
	}
}
