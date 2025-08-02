package com.SpringProject.kharidoMat.dto;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.Item;

import java.util.List;

public class ItemWithBookingsDTO {
    private Long id;
    private String title;
    private String description;
    private double pricePerDay;
    private boolean available;
    private String imageName;
    private List<Booking> bookings;

    public ItemWithBookingsDTO(Item item, List<Booking> bookings) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.pricePerDay = item.getPricePerDay();
        this.available = item.isAvailable();
        this.imageName = item.getImageName();
        this.bookings = bookings;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPricePerDay() { return pricePerDay; }
    public boolean isAvailable() { return available; }
    public String getImageName() { return imageName; }
    public List<Booking> getBookings() { return bookings; }
}
