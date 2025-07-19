package com.SpringProject.kharidoMat.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.service.BookingService;
import com.SpringProject.kharidoMat.service.EmailService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

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
        logger.info("Booking request by user '{}' for item ID {}", username, itemId);
        Booking booking = bookingService.createBooking(itemId, username, bookingRequest);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Fetching bookings for user '{}'", username);
        List<Booking> bookings = bookingService.getBookingByUser(username);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Booking>> getBookingsForOwner(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Fetching bookings where '{}' is the owner", username);
        List<Booking> bookings = bookingService.getBookingsForOwner(username);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        logger.info("User '{}' requested to cancel booking ID {}", username, id);
        Booking booking = bookingService.cancelBooking(id, username);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/extend/{id}")
    public ResponseEntity<?> extendBooking(@PathVariable Long id,
                                           @RequestParam String newEndDate,
                                           Authentication authentication) {
        String username = authentication.getName();
        logger.info("User '{}' is extending booking ID {} to new end date {}", username, id, newEndDate);
        LocalDate date = LocalDate.parse(newEndDate);
        Booking updated = bookingService.extendBooking(id, date, username);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/status-grouped")
    public ResponseEntity<?> getStatusGroupedBookings(Authentication authentication) {
        String email = authentication.getName();
        logger.info("Fetching bookings grouped by status for '{}'", email);
        Map<String, List<Booking>> grouped = bookingService.getBookingsGroupedByStatus(email);
        return ResponseEntity.ok(grouped);
    }

    @GetMapping("/return/{bookingId}")
    public ResponseEntity<String> confirmReturn(@PathVariable Long bookingId) {
        logger.info("Return confirmation initiated for booking ID {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setReturnStatus("pending_verification");
        booking.setReturned(false);
        bookingRepository.save(booking);
        return ResponseEntity.ok("Return pending lender verification");
    }

    @GetMapping("/returns/pending")
    public List<Booking> getPendingReturns() {
        logger.info("Fetching pending return bookings");
        return bookingRepository.findByReturnStatus("pending_verification");
    }

    @PostMapping("/return/verify/{bookingId}")
    public ResponseEntity<String> verifyReturn(@PathVariable Long bookingId,
                                               @RequestParam boolean accepted) {
        logger.info("Verifying return for booking ID {}. Accepted: {}", bookingId, accepted);
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

    @PostMapping("/return/request-otp/{bookingId}")
    public ResponseEntity<String> sendOtp(@PathVariable Long bookingId) {
        logger.info("Sending OTP for booking ID {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit OTP
        booking.setOtpCode(otp);
        booking.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        bookingRepository.save(booking);
        emailService.sendOtpEmail(booking.getUser().getEmail(), otp);

        logger.info("OTP sent to email: {}", booking.getUser().getEmail());
        return ResponseEntity.ok("OTP sent to registered email.");
    }

    @PostMapping("/return/verify-otp/{bookingId}")
    public ResponseEntity<String> verifyOtp(@PathVariable Long bookingId,
                                            @RequestParam String otp) {
        logger.info("Verifying OTP for booking ID {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (otp.equals(booking.getOtpCode()) &&
            LocalDateTime.now().isBefore(booking.getOtpExpiry())) {
            booking.setReturnStatus("pending_verification");
            booking.setReturned(false);
            booking.setOtpCode(null);
            booking.setOtpExpiry(null);
            bookingRepository.save(booking);
            logger.info("OTP verified for booking ID {}", bookingId);
            return ResponseEntity.ok("OTP verified. Return pending lender verification.");
        }

        logger.warn("Invalid or expired OTP for booking ID {}", bookingId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
    }
}
