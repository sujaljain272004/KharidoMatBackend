package com.SpringProject.kharidoMat.service;

import com.SpringProject.kharidoMat.model.Booking;

public interface BookingService {
    Booking createBooking(Long itemId, String username, Booking bookingRequest);
}