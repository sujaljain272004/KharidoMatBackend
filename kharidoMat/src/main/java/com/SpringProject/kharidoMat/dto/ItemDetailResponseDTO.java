package com.SpringProject.kharidoMat.dto;

import com.SpringProject.kharidoMat.model.Item;

public class ItemDetailResponseDTO {

	private Long id;

	private String title;

	private String description;

	private double pricePerDay;

	private String category;

	private OwnerDTO owner;

	private boolean available = true;

	private String imageName;

	public ItemDetailResponseDTO(Item item) {

		this.id = item.getId();
		this.title = item.getTitle();
		this.description = item.getDescription();
		this.pricePerDay = item.getPricePerDay();
		this.category = item.getCategory();
		this.available = item.isAvailable();
		this.imageName = item.getImageName();
		
		if (item.getUser() != null) {
			this.owner = new OwnerDTO(item.getUser());
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPricePerDay() {
		return pricePerDay;
	}

	public void setPricePerDay(double pricePerDay) {
		this.pricePerDay = pricePerDay;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public OwnerDTO getOwner() {
		return owner;
	}

	public void setOwner(OwnerDTO owner) {
		this.owner = owner;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	
}
