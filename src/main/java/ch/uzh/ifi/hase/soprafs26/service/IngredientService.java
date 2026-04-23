package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.constant.IngredientSeedData;
import ch.uzh.ifi.hase.soprafs26.entity.Ingredient;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class IngredientService {

	private final IngredientRepository ingredientRepository;

	public IngredientService(@Qualifier("ingredientRepository") IngredientRepository ingredientRepository) {
		this.ingredientRepository = ingredientRepository;
	}

	public List<Ingredient> getIngredients(User user) {
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
		}
		return ingredientRepository.findAllByUser(user);
	}

	public Ingredient createIngredient(Ingredient ingredient) {
		if (ingredient == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient payload must be provided");
		}
		if (ingredient.getIngredientName() == null || ingredient.getIngredientName().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient name must be provided");
		}
		if (ingredient.getUnit() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient unit must be provided");
		}
		if (ingredient.getUser() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ingredient must be associated with a user");
		}

		String normalizedName = ingredient.getIngredientName().trim();
		ingredient.setIngredientName(normalizedName);

		// Ensure uniqueness per user
		ingredientRepository.findByIngredientNameIgnoreCaseAndUser(normalizedName, ingredient.getUser()).ifPresent(existing -> {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					String.format("Ingredient '%s' already exists for this user", normalizedName));
		});

		return ingredientRepository.saveAndFlush(ingredient);
	}

	public void seedIngredients(User user) {
		for (IngredientSeedData.IngredientData seed : IngredientSeedData.INGREDIENTS) {
			Ingredient ingredient = new Ingredient();
			ingredient.setIngredientName(seed.getName());
            ingredient.setCategory(seed.getCategory());
			ingredient.setIngredientDescription("");
			ingredient.setUnit(seed.getUnit());
            ingredient.setUser(user);
			ingredientRepository.save(ingredient);
		}
		ingredientRepository.flush();
	}
}

