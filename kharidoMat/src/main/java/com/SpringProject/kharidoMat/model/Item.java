package com.SpringProject.kharidoMat.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;
     
    
    private double pricePerDay;

    private String category;

    private boolean available = true;
    
    private String imageName;
    
    

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



	public boolean isAvailable() {
		return available;
	}



	public void setAvailable(boolean available) {
		this.available = available;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}

    

	public String getImageName() {
		return imageName;
	}



	public void setImageName(String imageName) {
		this.imageName = imageName;
	}



	// Who posted this item
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("items")
    private User user;
    
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;



	public List<Booking> getBookings() {
		return bookings;
	}



	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

}
