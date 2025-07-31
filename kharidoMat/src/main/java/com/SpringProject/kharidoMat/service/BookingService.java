package com.SpringProject.kharidoMat.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.SpringProject.kharidoMat.dto.BookingDTO;
import com.SpringProject.kharidoMat.dto.BookingDateDto;
import com.SpringProject.kharidoMat.dto.BookingRequestDTO;
import com.SpringProject.kharidoMat.model.Booking;
import com.razorpay.RazorpayException;

public interface BookingService {
	Booking createBooking(BookingRequestDTO bookingRequest, String username);
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
	
	Map<String, Object> createExtensionPaymentOrder(Long bookingId, LocalDate newEndDate)throws RazorpayException;
	void verifyExtensionPaymentAndUpdateBooking(Long bookingId, LocalDate newEndDate, String paymentId, String orderId, String signature);

}