package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.Group;
import ch.uzh.ifi.hase.soprafs26.entity.ShoppingList;
import ch.uzh.ifi.hase.soprafs26.entity.ShoppingListItem;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.AuthUtil;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.GroupService;
import ch.uzh.ifi.hase.soprafs26.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Shopping List Controller
 *
 * All endpoints are scoped to the caller's group via /groups/my/shopping-list.
 * The caller must be authenticated (Bearer token) and a member of a group.
 */
@RestController
public class ShoppingListController {

	private final ShoppingListService shoppingListService;
	private final GroupService groupService;
	private final UserRepository userRepository;

	@Autowired
	public ShoppingListController(ShoppingListService shoppingListService,
			GroupService groupService,
			UserRepository userRepository) {
		this.shoppingListService = shoppingListService;
		this.groupService = groupService;
		this.userRepository = userRepository;
	}

	/**
	 * GET /groups/my/shopping-list — Get the group's shopping list.
	 */
	@GetMapping("/groups/my/shopping-list")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ShoppingListGetDTO getShoppingList(@RequestHeader("Authorization") String authHeader) {
		User caller = AuthUtil.authenticateUser(authHeader, userRepository);
		Group group = groupService.getGroupOfUser(caller.getId());
		ShoppingList shoppingList = shoppingListService.getShoppingListByGroupId(group.getId());
		return DTOMapper.INSTANCE.convertEntityToShoppingListGetDTO(shoppingList);
	}

	/**
	 * POST /groups/my/shopping-list/items — Add an item to the shopping list.
	 */
	@PostMapping("/groups/my/shopping-list/items")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ShoppingListItemGetDTO addItem(@RequestHeader("Authorization") String authHeader,
			@RequestBody ShoppingListItemPostDTO itemPostDTO) {
		User caller = AuthUtil.authenticateUser(authHeader, userRepository);
		Group group = groupService.getGroupOfUser(caller.getId());
		ShoppingList shoppingList = shoppingListService.getShoppingListByGroupId(group.getId());

		ShoppingListItem newItem = DTOMapper.INSTANCE.convertShoppingListItemPostDTOtoEntity(itemPostDTO);
		ShoppingListItem createdItem = shoppingListService.addItemToShoppingList(
				shoppingList.getId(), newItem, itemPostDTO.getIngredientId());
		return DTOMapper.INSTANCE.convertEntityToShoppingListItemGetDTO(createdItem);
	}

	/**
	 * GET /groups/my/shopping-list/items/{itemId} — Get a single item.
	 */
	@GetMapping("/groups/my/shopping-list/items/{itemId}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ShoppingListItemGetDTO getItem(@RequestHeader("Authorization") String authHeader,
			@PathVariable Long itemId) {
		User caller = AuthUtil.authenticateUser(authHeader, userRepository);
		Group group = groupService.getGroupOfUser(caller.getId());
		ShoppingListItem item = shoppingListService.getItemByIdAndVerifyGroup(itemId, group.getId());
		return DTOMapper.INSTANCE.convertEntityToShoppingListItemGetDTO(item);
	}

	/**
	 * PUT /groups/my/shopping-list/items/{itemId} — Update an item.
	 */
	@PutMapping("/groups/my/shopping-list/items/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateItem(@RequestHeader("Authorization") String authHeader,
			@PathVariable Long itemId,
			@RequestBody ItemPutDTO itemPutDTO) {
		User caller = AuthUtil.authenticateUser(authHeader, userRepository);
		Group group = groupService.getGroupOfUser(caller.getId());
		shoppingListService.getItemByIdAndVerifyGroup(itemId, group.getId());

		ShoppingListItem itemUpdate = new ShoppingListItem();
		itemUpdate.setQuantity(itemPutDTO.getQuantity());
		shoppingListService.updateItem(itemId, itemUpdate, itemPutDTO.getIngredientId());
	}

	/**
	 * PATCH /groups/my/shopping-list/items/{itemId} — Toggle bought status.
	 */
	@PatchMapping("/groups/my/shopping-list/items/{itemId}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ShoppingListItemGetDTO patchItemBoughtStatus(@RequestHeader("Authorization") String authHeader,
			@PathVariable Long itemId,
			@RequestBody ItemPatchDTO itemPatchDTO) {
		User caller = AuthUtil.authenticateUser(authHeader, userRepository);
		Group group = groupService.getGroupOfUser(caller.getId());
		shoppingListService.getItemByIdAndVerifyGroup(itemId, group.getId());

		ShoppingListItem updatedItem = shoppingListService.patchItemBoughtStatus(
				itemId, itemPatchDTO.getIsBought());
		return DTOMapper.INSTANCE.convertEntityToShoppingListItemGetDTO(updatedItem);
	}

	/**
	 * DELETE /groups/my/shopping-list/items/{itemId} — Remove an item.
	 */
	@DeleteMapping("/groups/my/shopping-list/items/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteItem(@RequestHeader("Authorization") String authHeader,
			@PathVariable Long itemId) {
		User caller = AuthUtil.authenticateUser(authHeader, userRepository);
		Group group = groupService.getGroupOfUser(caller.getId());
		shoppingListService.getItemByIdAndVerifyGroup(itemId, group.getId());

		shoppingListService.deleteItem(itemId);
	}
}
