package com.SpringProject.kharidoMat.model;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
        name = "item_category",
        joinColumns = @JoinColumn(name = "item_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnoreProperties("items")
    private Set<Category> categories = new HashSet<>();


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



	public Set<Category> getCategories() {
		return categories;
	}



	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
    
    
}