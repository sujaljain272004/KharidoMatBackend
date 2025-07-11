package com.SpringProject.kharidoMat.service;

import java.util.List;

import com.SpringProject.kharidoMat.model.Booking;

public interface BookingService {
    Booking createBooking(Long itemId, String username, Booking bookingRequest);
    List<Booking> getBookingByUser(String username);
    List<Booking> getBookingsForOwner(String username);
    Booking getBookingById(Long bookingId);

}