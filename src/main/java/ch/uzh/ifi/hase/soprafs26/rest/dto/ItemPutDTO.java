package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class ItemPutDTO {
	private Long ingredientId;
	private Integer quantity;
	private Double estimatedPrice;

	public Long getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(Long ingredientId) {
		this.ingredientId = ingredientId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getEstimatedPrice() {
		return estimatedPrice;
	}

	public void setEstimatedPrice(Double estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}
}
