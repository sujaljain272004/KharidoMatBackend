package com.SpringProject.kharidoMat.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@PostMapping("/item/{itemId}")
	public ResponseEntity<?> bookItem(@PathVariable Long itemId, @RequestBody Booking bookingRequest,
			Authentication authentication) {
		String username = authentication.getName();
		Booking booking = bookingService.createBooking(itemId, username, bookingRequest);
		return ResponseEntity.ok(booking);
	}

	@GetMapping("/my")
	public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
		String username = authentication.getName();
		List<Booking> bookings = bookingService.getBookingByUser(username);
		return ResponseEntity.ok(bookings);
	}

	@GetMapping("/owner")
	public ResponseEntity<List<Booking>> getBookingsForOwner(Authentication authentication) {
		String username = authentication.getName();
		List<Booking> bookings = bookingService.getBookingsForOwner(username);
		return ResponseEntity.ok(bookings);
	}

	@PutMapping("/cancel/{id}")
	public ResponseEntity<?> cancelBooking(@PathVariable Long id, Authentication authentication) {
		String username = authentication.getName();
		Booking booking = bookingService.cancelBooking(id, username);
		return ResponseEntity.ok(booking);
	}

	@PutMapping("/extend/{id}")
	public ResponseEntity<?> extendBooking(@PathVariable Long id, @RequestParam String newEndDate,
			Authentication authentication) {
		String username = authentication.getName();
		LocalDate date = LocalDate.parse(newEndDate);
		Booking updated = bookingService.extendBooking(id, date, username);
		return ResponseEntity.ok(updated);
	}

	@GetMapping("/status-grouped")
	public ResponseEntity<?> getStatusGroupedBookings(Authentication authentication) {
		String email = authentication.getName();
		Map<String, List<Booking>> grouped = bookingService.getBookingsGroupedByStatus(email);
		return ResponseEntity.ok(grouped);
	}

}