package com.SpringProject.kharidoMat.dto;

import com.SpringProject.kharidoMat.enums.BookingStatus;
import java.time.LocalDate;

// Add Getters and Setters for all fields
public class BookingDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalAmount;
    private BookingStatus status;
    private ItemDTO item;  // Nested Item details
    private UserDTO owner; // Nested Owner details
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BookingStatus getStatus() {
		return status;
	}
	public void setStatus(BookingStatus status) {
		this.status = status;
	}
	public ItemDTO getItem() {
		return item;
	}
	public void setItem(ItemDTO item) {
		this.item = item;
	}
	public UserDTO getOwner() {
		return owner;
	}
	public void setOwner(UserDTO owner) {
		this.owner = owner;
	}
    
    
}