package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.util.List;

public class ShoppingListGetDTO {
	private Long id;
	private Long groupId;
	private Double totalEstimatedCost;
	private List<ShoppingListItemGetDTO> items;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Double getTotalEstimatedCost() {
		return totalEstimatedCost;
	}

	public void setTotalEstimatedCost(Double totalEstimatedCost) {
		this.totalEstimatedCost = totalEstimatedCost;
	}

	public List<ShoppingListItemGetDTO> getItems() {
		return items;
	}

	public void setItems(List<ShoppingListItemGetDTO> items) {
		this.items = items;
	}
}
