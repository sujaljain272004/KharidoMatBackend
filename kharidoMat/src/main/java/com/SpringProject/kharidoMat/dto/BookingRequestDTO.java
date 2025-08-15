package com.SpringProject.kharidoMat.dto;

import java.time.LocalDate;

public class BookingRequestDTO {
    private Long itemId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalPrice; // total booking price
    private Double depositAmount; // NEW: base deposit amount
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature; // NEW: for verification
    private String notes; // NEW: optional booking notes

    // --- Getters and Setters ---
    public Long getItemId() {
        return itemId;
    }
    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public Double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDepositAmount() {
        return depositAmount;
    }
    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }
    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }
    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
