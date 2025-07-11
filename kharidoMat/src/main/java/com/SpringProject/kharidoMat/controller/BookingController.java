package com.SpringProject.kharidoMat.controller;


import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.service.BookingService;
import com.SpringProject.kharidoMat.service.EmailService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
	

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/item/{itemId}")
    public ResponseEntity<?> bookItem(@PathVariable Long itemId,
                                      @RequestBody Booking bookingRequest,
                                      Authentication authentication) {
        String username = authentication.getName(); 
        Booking booking = bookingService.createBooking(itemId, username, bookingRequest);
        return ResponseEntity.ok(booking);
    }
    
    
    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication){
    	String username = authentication.getName();
        List<Booking> bookings = bookingService.getBookingByUser(username);
        return ResponseEntity.ok(bookings);
    }
    
	@GetMapping("/owner")
	public ResponseEntity<List<Booking>> getBookingsForOwner(Authentication authentication){
		String username= authentication.getName();
		List<Booking> bookings = bookingService.getBookingsForOwner(username);
		return ResponseEntity.ok(bookings);
	}

	 
	 @GetMapping("/return/{bookingId}")
	 public ResponseEntity<String> confirmReturn(@PathVariable Long bookingId) {
	     Booking booking = bookingRepository.findById(bookingId).orElseThrow();
	     booking.setReturnStatus("pending_verification");
	     booking.setReturned(false); 
	     bookingRepository.save(booking);
	     return ResponseEntity.ok("Return pending lender verification");
	 }
	 
	//Get pending returns for verification
	 @GetMapping("/returns/pending")
	 public List<Booking> getPendingReturns() {
	     return bookingRepository.findByReturnStatus("pending_verification");
	 }

	 // Lender verifies return
	 @PostMapping("/return/verify/{bookingId}")
	 public ResponseEntity<String> verifyReturn(@PathVariable Long bookingId, @RequestParam boolean accepted) {
	     Booking booking = bookingRepository.findById(bookingId).orElseThrow();

	     if (accepted) {
	         booking.setReturned(true);
	         booking.setReturnStatus("confirmed");
	     } else {
	         booking.setReturnStatus("rejected");
	     }

	     bookingRepository.save(booking);
	     return ResponseEntity.ok("Return " + (accepted ? "confirmed" : "rejected"));
	 }
	 
	 //opt for return
	 @PostMapping("/return/request-otp/{bookingId}")
	 public ResponseEntity<String> sendOtp(@PathVariable Long bookingId) {
	     Booking booking = bookingRepository.findById(bookingId).orElseThrow();
	     
	     String otp = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit OTP
	     booking.setOtpCode(otp);
	     booking.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

	     bookingRepository.save(booking);
	     emailService.sendOtpEmail(booking.getUser().getEmail(), otp);

	     return ResponseEntity.ok("OTP sent to registered email.");
	 }
	 
	 @PostMapping("/return/verify-otp/{bookingId}")
	 public ResponseEntity<String> verifyOtp(@PathVariable Long bookingId, @RequestParam String otp) {
	     Booking booking = bookingRepository.findById(bookingId).orElseThrow();

	     if (otp.equals(booking.getOtpCode()) && LocalDateTime.now().isBefore(booking.getOtpExpiry())) {
	         booking.setReturnStatus("pending_verification");
	         booking.setReturned(false);
	         booking.setOtpCode(null);
	         booking.setOtpExpiry(null);
	         bookingRepository.save(booking);
	         return ResponseEntity.ok("OTP verified. Return pending lender verification.");
	     }

	     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
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