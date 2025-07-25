package com.SpringProject.kharidoMat.dto;

import java.time.LocalDate;

public class BookingDateDto {

    private LocalDate startDate;
    private LocalDate endDate;

    // Default constructor
    public BookingDateDto() {
    }

    // Constructor with fields
    public BookingDateDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
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
}