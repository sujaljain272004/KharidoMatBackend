package com.SpringProject.kharidoMat.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.SpringProject.kharidoMat.dto.BookingDTO;
import com.SpringProject.kharidoMat.dto.BookingDateDto;
import com.SpringProject.kharidoMat.model.Booking;

public interface BookingService {
	Booking createBooking(Long itemId, String username, LocalDate startDate, LocalDate endDate);

	// The new, correct return type
	List<BookingDTO> getBookingByUser(String username);

	List<Booking> getBookingsForOwner(String username);

	Booking getBookingById(Long bookingId);

	Booking cancelBooking(Long bookingId, String username);

	Booking extendBooking(Long bookingId, LocalDate newEndDate, String username);

	Map<String, List<Booking>> getBookingsGroupedByStatus(String email);
	
	List<Booking> getBookingsByUserId(Long userId);
	
	// In BookingService.java interface
	List<BookingDTO> getPendingReturnsForOwner(String ownerUsername);
	
	List<BookingDateDto> getBookingDatesByItemId(Long itemId) ;

}