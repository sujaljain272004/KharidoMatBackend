package com.SpringProject.kharidoMat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@JsonIgnore
	@ManyToMany(mappedBy = "categories")
	private Set<Item> items = new HashSet<>();

	private double baseDeposit;

	public Category() {
	}

	public Category(String name) {
	    this.name = name;
	}

	public Category(String name,double baseDeposit) {
		this.name = name;
		this.baseDeposit = baseDeposit;
	}

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Item> getItems() {
		return items;
	}

	public void setItems(Set<Item> items) {
		this.items = items;
	}

	public double getBaseDeposit() {
		return baseDeposit;
	}

	public void setBaseDeposit(double baseDeposit) {
		this.baseDeposit = baseDeposit;
	}

}
