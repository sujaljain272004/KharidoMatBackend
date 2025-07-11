package com.SpringProject.kharidoMat.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.SpringProject.kharidoMat.model.Booking;

public interface BookingService {
    Booking createBooking(Long itemId, String username, Booking bookingRequest);
    List<Booking> getBookingByUser(String username);
    List<Booking> getBookingsForOwner(String username);
    Booking getBookingById(Long bookingId);
    Booking cancelBooking(Long bookingId, String username);
    Booking extendBooking(Long bookingId, LocalDate newEndDate, String username);
    Map<String, List<Booking>> getBookingsGroupedByStatus(String email);

}