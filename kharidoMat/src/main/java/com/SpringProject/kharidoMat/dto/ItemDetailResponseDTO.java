package com.SpringProject.kharidoMat.dto;

import java.util.List;

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

	private Double averageRating;
	private Long totalReviews;
	private List<BookingDTO> bookings;

	public List<BookingDTO> getBookings() {
	    return bookings;
	}

	public void setBookings(List<BookingDTO> bookings) {
	    this.bookings = bookings;
	}


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
	
	public Double getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    public Long getTotalReviews() {
        return totalReviews;
    }
    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

}
