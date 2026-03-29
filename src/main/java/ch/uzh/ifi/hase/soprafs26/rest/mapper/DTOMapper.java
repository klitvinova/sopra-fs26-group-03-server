package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;

import ch.uzh.ifi.hase.soprafs26.entity.ShoppingList;
import ch.uzh.ifi.hase.soprafs26.entity.ShoppingListItem;
import ch.uzh.ifi.hase.soprafs26.rest.dto.*;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

	DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

	@Mapping(source = "name", target = "name")
	@Mapping(source = "username", target = "username")
	User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "status", target = "status")
	UserGetDTO convertEntityToUserGetDTO(User user);

	@Mapping(source = "groupId", target = "groupId")
	ShoppingList convertShoppingListPostDTOtoEntity(ShoppingListPostDTO shoppingListPostDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "groupId", target = "groupId")
	@Mapping(source = "items", target = "items")
	ShoppingListGetDTO convertEntityToShoppingListGetDTO(ShoppingList shoppingList);

	@Mapping(source = "ingredientId", target = "ingredient.id")
	@Mapping(source = "quantity", target = "quantity")
	ShoppingListItem convertShoppingListItemPostDTOtoEntity(ShoppingListItemPostDTO shoppingListItemPostDTO);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "quantity", target = "quantity")
	@Mapping(source = "isBought", target = "isBought")
	@Mapping(source = "ingredient.id", target = "ingredientId")
	@Mapping(source = "ingredient.ingredientName", target = "ingredientName")
	@Mapping(source = "ingredient.unit", target = "unit")
	ShoppingListItemGetDTO convertEntityToShoppingListItemGetDTO(ShoppingListItem shoppingListItem);
}
