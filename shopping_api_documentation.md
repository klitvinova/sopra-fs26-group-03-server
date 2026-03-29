# Shopping List API Documentation

## Shopping Lists

### Get All Shopping Lists
- **URL**: `/shoppingLists`
- **Method**: `GET`
- **Response Body**: `List<ShoppingListGetDTO>`

### Create Shopping List
- **URL**: `/shoppingLists`
- **Method**: `POST`
- **Request Body**: [ShoppingListPostDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/mapper/DTOMapper.java#40-42)
  - `groupId` (Long): The ID of the group this list belongs to.
- **Response Body**: [ShoppingListGetDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/dto/ShoppingListGetDTO.java#5-35)

### Get Shopping List by ID
- **URL**: `/shoppingLists/{id}`
- **Method**: `GET`
- **Response Body**: [ShoppingListGetDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/dto/ShoppingListGetDTO.java#5-35)

### Update Shopping List
- **URL**: `/shoppingLists/{id}`
- **Method**: `PUT`
- **Request Body**: [ShoppingListPostDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/mapper/DTOMapper.java#40-42)
- **Response**: `204 No Content`

### Delete Shopping List
- **URL**: `/shoppingLists/{id}`
- **Method**: `DELETE`
- **Response**: `204 No Content`

## Shopping List Items

### Add Item to Shopping List
- **URL**: `/shoppingLists/{listId}/items`
- **Method**: `POST`
- **Request Body**: [ShoppingListItemPostDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/dto/ShoppingListItemPostDTO.java#3-24)
  - `ingredientId` (Long): The ID of the ingredient.
  - `quantity` (Integer): The quantity of the ingredient.
- **Response Body**: [ShoppingListItemGetDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/dto/ShoppingListItemGetDTO.java#5-62)

### Get Item by ID
- **URL**: `/shoppingLists/items/{itemId}`
- **Method**: `GET`
- **Response Body**: [ShoppingListItemGetDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/dto/ShoppingListItemGetDTO.java#5-62)

### Update Item
- **URL**: `/shoppingLists/items/{itemId}`
- **Method**: `PUT`
- **Request Body**: [ItemPutDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/dto/ItemPutDTO.java#3-24)
  - `ingredientId` (Long): The ID of the ingredient.
  - `quantity` (Integer): The quantity.
- **Response**: `204 No Content`

### Patch Item Bought Status
- **URL**: `/shoppingLists/items/{itemId}`
- **Method**: `PATCH`
- **Request Body**: `ItemPatchDTO`
  - `isBought` (Boolean): The new status.
- **Response Body**: [ShoppingListItemGetDTO](file:///Users/karina/Local/UZH_study/SoPra/PlateMate/src/main/java/ch/uzh/ifi/hase/soprafs26/rest/dto/ShoppingListItemGetDTO.java#5-62)

### Delete Item
- **URL**: `/shoppingLists/items/{itemId}`
- **Method**: `DELETE`
- **Response**: `204 No Content`

