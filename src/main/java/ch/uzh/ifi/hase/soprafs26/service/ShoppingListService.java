package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Ingredient;
import ch.uzh.ifi.hase.soprafs26.entity.Pantry;
import ch.uzh.ifi.hase.soprafs26.entity.ShoppingList;
import ch.uzh.ifi.hase.soprafs26.entity.ShoppingListItem;
import ch.uzh.ifi.hase.soprafs26.repository.IngredientRepository;
import ch.uzh.ifi.hase.soprafs26.repository.PantryRepository;
import ch.uzh.ifi.hase.soprafs26.repository.ShoppingListItemRepository;
import ch.uzh.ifi.hase.soprafs26.repository.ShoppingListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ShoppingListService {

	private final Logger log = LoggerFactory.getLogger(ShoppingListService.class);

	private final ShoppingListRepository shoppingListRepository;
	private final ShoppingListItemRepository shoppingListItemRepository;
	private final IngredientRepository ingredientRepository;
	private final PantryRepository pantryRepository;
	private final PantryService pantryService;

	@Autowired
	public ShoppingListService(@Qualifier("shoppingListRepository") ShoppingListRepository shoppingListRepository,
			@Qualifier("shoppingListItemRepository") ShoppingListItemRepository shoppingListItemRepository,
			@Qualifier("ingredientRepository") IngredientRepository ingredientRepository,
			@Qualifier("pantryRepository") PantryRepository pantryRepository,
			PantryService pantryService) {
		this.shoppingListRepository = shoppingListRepository;
		this.shoppingListItemRepository = shoppingListItemRepository;
		this.ingredientRepository = ingredientRepository;
		this.pantryRepository = pantryRepository;
		this.pantryService = pantryService;
	}

	/**
	 * Get the shopping list for a specific group.
	 *
	 * @param groupId the group's ID
	 * @return the shopping list belonging to that group
	 */
	public ShoppingList getShoppingListByGroupId(Long groupId) {
		List<ShoppingList> lists = shoppingListRepository.findAllByGroupId(groupId);
		if (lists.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"No shopping list found for this group");
		}
		return lists.get(0); // each group has exactly one shopping list
	}

	public ShoppingList getShoppingListById(Long id) {
		return shoppingListRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping list not found"));
	}

	public ShoppingListItem addItemToShoppingList(Long listId, ShoppingListItem newItem, Long ingredientId) {
		ShoppingList shoppingList = getShoppingListById(listId);
		Ingredient ingredient = ingredientRepository.findById(ingredientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

		newItem.setShoppingList(shoppingList);
		newItem.setIngredient(ingredient);
		shoppingList.getItems().add(newItem);

		newItem = shoppingListItemRepository.save(newItem);
		shoppingListItemRepository.flush();
		return newItem;
	}

	public ShoppingListItem getItemById(Long id) {
		return shoppingListItemRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
	}

	/**
	 * Get an item and verify it belongs to the given group's shopping list.
	 */
	public ShoppingListItem getItemByIdAndVerifyGroup(Long itemId, Long groupId) {
		ShoppingListItem item = getItemById(itemId);
		if (!item.getShoppingList().getGroupId().equals(groupId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"This item does not belong to your group's shopping list");
		}
		return item;
	}

	public void updateItem(Long itemId, ShoppingListItem itemUpdate, Long ingredientId) {
		ShoppingListItem item = getItemById(itemId);
		Ingredient ingredient = ingredientRepository.findById(ingredientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

		item.setQuantity(itemUpdate.getQuantity());
		item.setIngredient(ingredient);

		shoppingListItemRepository.save(item);
		shoppingListItemRepository.flush();
	}

	/**
	 * Toggle the bought status of an item.
	 * When marked as bought (isBought = true), the item is automatically
	 * moved to the group's pantry and removed from the shopping list.
	 */
	public ShoppingListItem patchItemBoughtStatus(Long itemId, Boolean isBought) {
		ShoppingListItem item = getItemById(itemId);
		item.setIsBought(isBought);
		item = shoppingListItemRepository.save(item);
		shoppingListItemRepository.flush();

		// auto-move to pantry when marked as bought
		if (Boolean.TRUE.equals(isBought)) {
			Long groupId = item.getShoppingList().getGroupId();
			Pantry pantry = pantryRepository.findAllByGroupId(groupId).stream()
					.findFirst()
					.orElse(null);
			if (pantry != null) {
				pantryService.addItemToPantry(
						pantry.getId(),
						item.getIngredient().getId(),
						item.getQuantity());

				// remove from shopping list
				ShoppingList shoppingList = item.getShoppingList();
				shoppingList.getItems().remove(item);
				shoppingListItemRepository.delete(item);
				shoppingListItemRepository.flush();
			}
		}

		return item;
	}

	public void deleteItem(Long itemId) {
		ShoppingListItem item = getItemById(itemId);
		ShoppingList shoppingList = item.getShoppingList();
		shoppingList.getItems().remove(item);

		shoppingListItemRepository.delete(item);
		shoppingListItemRepository.flush();
	}

}
