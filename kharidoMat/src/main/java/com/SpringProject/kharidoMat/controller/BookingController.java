package com.SpringProject.kharidoMat.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.dto.BookingDTO;
import com.SpringProject.kharidoMat.dto.BookingDateDto;
import com.SpringProject.kharidoMat.dto.BookingRequestDTO;
import com.SpringProject.kharidoMat.dto.ReturnRequestDTO;
import com.SpringProject.kharidoMat.enums.BookingStatus;
import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.BookingService;
import com.SpringProject.kharidoMat.service.EmailService;
import com.SpringProject.kharidoMat.service.ReportService;
import com.razorpay.RazorpayException;

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
    
    @Autowired
    private ReportService reportService;


    @Autowired
    private UserRepository userRepository;


    @GetMapping("/my")
    public ResponseEntity<List<BookingDTO>> getMyBookings(Authentication authentication) {
        String username = authentication.getName();
        logger.info("Fetching bookings for user '{}'", username);
        List<BookingDTO> bookings = bookingService.getBookingByUser(username);
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
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingDetails(@PathVariable Long bookingId) {
        BookingDTO bookingDto = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(bookingDto);
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

 // In your BookingController.java

 // Make sure to import ReturnRequestDTO and Map

 // NEW, REFACTORED METHOD:
 @PostMapping("/return/verify/{bookingId}")
 public ResponseEntity<?> verifyReturn(@PathVariable Long bookingId,
                                       @RequestBody ReturnRequestDTO request) {
     try {
         // All logic is now handled by the service layer
         bookingService.processItemReturn(bookingId, request.isAccepted(), request.getNotes());
         
         String message = request.isAccepted() 
             ? "Return confirmed and refund initiated." 
             : "Return rejected and deposit forfeited.";
             
         return ResponseEntity.ok(Map.of("message", message));

     } catch (Exception e) {
         logger.error("Error processing return for booking {}: {}", bookingId, e.getMessage());
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
     }
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

 // In your BookingController.java

 // In your BookingController.java

    @PostMapping("/return/verify-otp/{bookingId}")
    public ResponseEntity<String> verifyOtp(@PathVariable Long bookingId,
                                            @RequestParam String otp) {
        logger.info("Verifying OTP for booking ID {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(RuntimeException::new);

        logger.info("Expected OTP: {}", booking.getOtpCode());
        logger.info("Received OTP: {}", otp);

        if (booking.getOtpCode() != null &&
            otp.equals(booking.getOtpCode()) &&
            booking.getOtpExpiry() != null &&
            LocalDateTime.now().isBefore(booking.getOtpExpiry())) {

            booking.setReturnStatus("pending_verification");
            booking.setReturned(false);
            booking.setOtpCode(null);
            booking.setOtpExpiry(null);

            bookingRepository.save(booking);

            return ResponseEntity.ok("OTP verified. Return is now pending owner verification.");
        }

        logger.warn("Invalid or expired OTP for booking ID {}", bookingId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
    }

    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable Long userId) {
        logger.info("Fetching bookings for user ID {}", userId);
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }
    
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequest, 
                                           Authentication authentication) {
    	
    	logger.info("Service received Razorpay Payment ID: {}", bookingRequest.getRazorpayPaymentId());
        logger.info("Service received Razorpay Order ID: {}", bookingRequest.getRazorpayOrderId());
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User must be logged in.");
        }

        String userEmail = authentication.getName();
        logger.info("Booking request by user '{}' for item ID {}", userEmail, bookingRequest.getItemId());

        try {
        	Booking booking = bookingService.createBooking(bookingRequest, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (IllegalArgumentException e) {
            logger.warn("Booking creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during booking creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }
    
 // In BookingController.java

    @GetMapping("/returns/pending-for-owner")
    public ResponseEntity<List<BookingDTO>> getPendingReturnsForOwner(Authentication authentication) {
        // 1. Get the currently logged-in user's email (who is the item owner)
        String ownerUsername = authentication.getName();
        logger.info("Fetching pending returns for owner '{}'", ownerUsername);

        // 2. Call the new service method you just created
        List<BookingDTO> pendingReturns = bookingService.getPendingReturnsForOwner(ownerUsername);

        // 3. Return the list of pending bookings to the frontend
        return ResponseEntity.ok(pendingReturns);
    }
    
    @GetMapping("/{itemId}/bookings")
    public ResponseEntity<List<BookingDateDto>> getItemBookings(@PathVariable Long itemId) {
        List<BookingDateDto> bookingDates = bookingService.getBookingDatesByItemId(itemId);
        return ResponseEntity.ok(bookingDates);
    }
    
 // In your BookingController.java

    @PostMapping("/{bookingId}/create-extension-order")
    public ResponseEntity<Map<String, Object>> createExtensionOrder(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> payload) throws RazorpayException {
        
        String newEndDateStr = payload.get("newEndDate");
        LocalDate newEndDate = LocalDate.parse(newEndDateStr);

        // This service method will calculate the cost and create a Razorpay order
        Map<String, Object> razorpayOrderDetails = bookingService.createExtensionPaymentOrder(bookingId, newEndDate);
        
        return ResponseEntity.ok(razorpayOrderDetails);
    }
    
 // In your BookingController.java

    @PostMapping("/{bookingId}/verify-and-extend")
    public ResponseEntity<String> verifyAndExtendBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> payload) {

        String newEndDateStr = payload.get("newEndDate");
        String razorpayPaymentId = payload.get("razorpay_payment_id");
        String razorpayOrderId = payload.get("razorpay_order_id");
        String razorpaySignature = payload.get("razorpay_signature");
        
        bookingService.verifyExtensionPaymentAndUpdateBooking(
            bookingId, 
            LocalDate.parse(newEndDateStr),
            razorpayPaymentId,
            razorpayOrderId,
            razorpaySignature
        );

        return ResponseEntity.ok("Booking extended successfully!");
    }
    
    
    @GetMapping("/generate-test-report")
    public ResponseEntity<String> generateTestReport(@RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            YearMonth currentMonth = YearMonth.now(); 
            
            byte[] report = reportService.generateMonthlyReportForOwner(user, currentMonth);
            
            if (report.length > 50) { 
                emailService.sendMonthlyReportEmail(user, currentMonth, report);
                return ResponseEntity.ok("SUCCESS: Test report for user " + userId + " has been sent.");
            } else {
                return ResponseEntity.ok("INFO: No completed booking data found for this user in the current month. Report was not sent.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("ERROR: Failed to generate report: " + e.getMessage());
        }
    }

}