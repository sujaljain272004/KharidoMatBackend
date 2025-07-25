package com.SpringProject.kharidoMat.serviceImpl;

import java.time.LocalDate;
import com.SpringProject.kharidoMat.dto.BookingDTO; // Import DTOs
import com.SpringProject.kharidoMat.dto.BookingDateDto;
import com.SpringProject.kharidoMat.dto.ItemDTO;
import com.SpringProject.kharidoMat.dto.UserDTO;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.enums.BookingStatus;
import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.BookingService;
import com.SpringProject.kharidoMat.service.EmailService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Service
public class BookingServiceImpl implements BookingService {

	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Override
	public Booking createBooking(Long itemId, String username, LocalDate startDate, LocalDate endDate) {
		logger.info("Service: Creating booking for item {} by user {} from {} to {}", itemId, username, startDate,
				endDate);

		// 1. Find the User and Item from the database
		// CORRECTED: Handle a nullable User object instead of an Optional
		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new IllegalArgumentException("User not found with email: " + username);
		}

		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

		// 2. Perform validation checks
		if (item.getUser().getEmail().equals(username)) {
			logger.warn("User {} attempted to book their own item {}", username, itemId);
			throw new IllegalArgumentException("You cannot book your own item.");
		}

		List<Booking> conflicts = bookingRepository.findConflictingBookings(itemId, startDate, endDate);
		if (!conflicts.isEmpty()) {
			logger.warn("Booking conflict for item {} between {} and {}", itemId, startDate, endDate);
			throw new IllegalArgumentException("Item is already booked for the selected dates.");
		}

		// 3. Create and populate the new Booking object
		Booking newBooking = new Booking();
		newBooking.setUser(user);
		newBooking.setItem(item);
		newBooking.setStartDate(startDate);
		newBooking.setEndDate(endDate);
		newBooking.setStatus(BookingStatus.ACTIVE); // Or PENDING, CONFIRMED, etc.

		// 4. Calculate the total price
		long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Add 1 to include the start day
		double pricePerDay = item.getPricePerDay();
		newBooking.setAmount(days * pricePerDay);

		// 5. Save the new booking and return it
		logger.info("Booking created successfully: itemId={}, user={}, amount={}", itemId, username,
				newBooking.getAmount());
		return bookingRepository.save(newBooking);
	}

	// In BookingService.java

	@Override
	public List<BookingDTO> getBookingByUser(String username) {
		logger.info("Fetching bookings for user {}", username);

		User user = userRepository.findByEmail(username);
		if (user == null) {
			logger.error("User not found: {}", username);
			throw new UsernameNotFoundException("User not found");
		}

		List<Booking> bookings = bookingRepository.findByUser(user);

		return bookings.stream().map(booking -> {

			logger.info("Mapping booking ID {}. Owner Name from DB: '{}'", booking.getId(),
					booking.getItem().getUser().getFullName());
			BookingDTO dto = new BookingDTO();
			dto.setId(booking.getId());
			dto.setStartDate(booking.getStartDate());
			dto.setEndDate(booking.getEndDate());
			dto.setTotalAmount(booking.getAmount());
			dto.setStatus(booking.getStatus());

			ItemDTO itemDto = new ItemDTO();
			itemDto.setId(booking.getItem().getId());
			// FIX #1: Use getTitle() instead of getName()
			itemDto.setName(booking.getItem().getTitle());
			// FIX #2: Use getImageName() instead of getImageUrl()
			itemDto.setImageUrl(booking.getItem().getImageName());
			itemDto.setPricePerDay(booking.getItem().getPricePerDay());
			dto.setItem(itemDto);

			UserDTO ownerDto = new UserDTO(user);
			ownerDto.setId(booking.getItem().getUser().getId());
			ownerDto.setFullName(booking.getItem().getUser().getFullName());
			ownerDto.setEmail(booking.getItem().getUser().getEmail());
			dto.setOwner(ownerDto);

			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public List<Booking> getBookingsForOwner(String username) {
		logger.info("Fetching bookings for item owner {}", username);

		User user = userRepository.findByEmail(username);
		if (user == null) {
			logger.error("User not found: {}", username);
			throw new UsernameNotFoundException("User not found");
		}

		return bookingRepository.findBookingsByItemOwner(user);
	}

	@Scheduled(cron = "0 0 8 * * ?")
	public void sendReminderEmails() {
		logger.info("Running scheduled task: sendReminderEmails");

		LocalDate tomorrow = LocalDate.now().plusDays(1);
		List<Booking> bookings = bookingRepository.findByEndDate(tomorrow);

		for (Booking booking : bookings) {
			String to = booking.getUser().getEmail();
			String itemName = booking.getItem().getTitle();
			String body = "Dear " + booking.getUser().getFullName() + ",\n\n"
					+ "This is a reminder to return the item: " + itemName + " by tomorrow (" + booking.getEndDate()
					+ ").\n\nThanks!\nCampusRent Team";

			emailService.sendEmail(to, "Return Reminder: " + itemName, body);
			logger.info("Reminder email sent to {} for item {}", to, itemName);
		}
	}

	@Override
	public Booking getBookingById(Long bookingId) {
		logger.info("Fetching booking by ID: {}", bookingId);

		return bookingRepository.findById(bookingId).orElseThrow(() -> {
			logger.error("Booking not found: {}", bookingId);
			return new RuntimeException("Booking not found");
		});
	}

	@Override
	public Booking cancelBooking(Long bookingId, String username) {
		logger.info("Cancel booking request by {} for booking {}", username, bookingId);

		Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
			logger.error("Booking not found: {}", bookingId);
			return new RuntimeException("Booking not found");
		});

		if (!booking.getUser().getEmail().equals(username)) {
			logger.warn("Unauthorized cancel attempt by {}", username);
			throw new RuntimeException("You are not allowed to cancel this booking");
		}

		if (LocalDate.now().isAfter(booking.getStartDate())) {
			logger.warn("Cancel denied: Booking {} already started", bookingId);
			throw new RuntimeException("Cannot cancel booking after start date");
		}

		booking.setStatus(BookingStatus.CANCELED);
		logger.info("Booking {} canceled by {}", bookingId, username);
		return bookingRepository.save(booking);
	}

	@Override
	public Booking extendBooking(Long bookingId, LocalDate newEndDate, String username) {
		logger.info("Extend booking request: booking={}, newEndDate={}, user={}", bookingId, newEndDate, username);

		Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
			logger.error("Booking not found: {}", bookingId);
			return new RuntimeException("Booking not found");
		});

		if (!booking.getUser().getEmail().equals(username)) {
			logger.warn("Unauthorized extension attempt by {}", username);
			throw new RuntimeException("Unauthorized");
		}

		if (booking.getStatus() != BookingStatus.ACTIVE) {
			logger.warn("Cannot extend: Booking {} is not ACTIVE", bookingId);
			throw new RuntimeException("Only ACTIVE bookings can be extended");
		}

		if (!newEndDate.isAfter(booking.getEndDate())) {
			logger.warn("Invalid extension date for booking {} by {}", bookingId, username);
			throw new RuntimeException("New end date must be after current end date");
		}

		List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(booking.getItem().getId(),
				booking.getEndDate().plusDays(1), newEndDate);

		if (!conflictingBookings.isEmpty()) {
			logger.warn("Extension conflict for booking {} during {}", bookingId, newEndDate);
			throw new RuntimeException("Item is already booked during this extension period");
		}

		booking.setEndDate(newEndDate);
		logger.info("Booking {} extended by {} to {}", bookingId, username, newEndDate);
		return bookingRepository.save(booking);
	}

	@Override
	public Map<String, List<Booking>> getBookingsGroupedByStatus(String email) {
		logger.info("Fetching grouped bookings for {}", email);

		User user = userRepository.findByEmail(email);
		if (user == null) {
			logger.error("User not found: {}", email);
			throw new RuntimeException("User not found");
		}

		LocalDate today = LocalDate.now();

		Map<String, List<Booking>> result = new HashMap<>();
		result.put("upcoming", bookingRepository.findUpcomingBookings(user, today));
		result.put("ongoing", bookingRepository.findOngoingBookings(user, today));
		result.put("past", bookingRepository.findPastBookings(user, today));

		logger.info("Grouped bookings retrieved for {}", email);
		return result;
	}

	public List<Booking> getBookingsByUserId(Long userId) {
		return bookingRepository.findByUserId(userId);
	}

	// In BookingServiceImpl.java

	public List<BookingDTO> getPendingReturnsForOwner(String ownerUsername) {
		logger.info("Fetching pending returns for owner '{}'", ownerUsername);

		// Find the owner from the database
		User owner = userRepository.findByEmail(ownerUsername);
		if (owner == null) {
			throw new UsernameNotFoundException("Owner not found with email: " + ownerUsername);
		}

		// Get all bookings that are awaiting verification
		List<Booking> pendingBookings = bookingRepository.findByReturnStatus("pending_verification");

		// Filter this list to include only items owned by the current user
		// And convert them to DTOs to send to the frontend
		return pendingBookings.stream().filter(booking -> booking.getItem().getUser().equals(owner)).map(booking -> {
			// This is the same DTO mapping logic from your getBookingByUser method
			BookingDTO dto = new BookingDTO();
			dto.setId(booking.getId());
			dto.setStartDate(booking.getStartDate());
			dto.setEndDate(booking.getEndDate());
			dto.setTotalAmount(booking.getAmount());
			dto.setStatus(booking.getStatus());

			ItemDTO itemDto = new ItemDTO();
			itemDto.setId(booking.getItem().getId());
			itemDto.setName(booking.getItem().getTitle());
			itemDto.setImageUrl(booking.getItem().getImageName());
			itemDto.setPricePerDay(booking.getItem().getPricePerDay());
			dto.setItem(itemDto);

			// Here, the 'owner' is still the item owner, which is the current user
			UserDTO ownerDto = new UserDTO(owner);
			ownerDto.setId(booking.getItem().getUser().getId());
			ownerDto.setFullName(booking.getItem().getUser().getFullName());
			ownerDto.setEmail(booking.getItem().getUser().getEmail());
			dto.setOwner(ownerDto);

			return dto;
		}).collect(Collectors.toList());
	}

	public List<BookingDateDto> getBookingDatesByItemId(Long itemId) {
		// 1. Fetch all booking entities for the item from the database.
		List<Booking> bookings = bookingRepository.findAllByItemId(itemId);

		// 2. Map the list of Booking entities to a list of BookingDateDto objects.
		return bookings.stream().map(booking -> new BookingDateDto(booking.getStartDate(), booking.getEndDate()))
				.collect(Collectors.toList());
	}

	// In your BookingServiceImpl.java

	public Map<String, Object> createExtensionPaymentOrder(Long bookingId, LocalDate newEndDate)
			throws RazorpayException {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		// 1. Calculate the number of extra days
		long extraDays = ChronoUnit.DAYS.between(booking.getEndDate(), newEndDate);

		if (extraDays <= 0) {
			throw new IllegalArgumentException("New end date must be after the current end date.");
		}

		// 2. Calculate the cost for the extension
		double extensionAmount = extraDays * booking.getItem().getPricePerDay();

		// 3. Create a Razorpay order (you likely have similar logic for initial
		// bookings)
		RazorpayClient razorpay = new RazorpayClient("rzp_test_n5Y0q2oWkbhx2b", "SJav5iDCiTd3x43xN3U4ru1Z");

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", extensionAmount * 100); // Amount in paise
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", "receipt_extension_" + booking.getId());

		Order order = razorpay.orders.create(orderRequest);

		// 4. Return the details to the frontend
		Map<String, Object> response = new HashMap<>();
		response.put("orderId", order.get("id"));
		response.put("amount", order.get("amount"));
		response.put("currency", order.get("currency"));

		return response;
	}

	// In your BookingServiceImpl.java

	public void verifyExtensionPaymentAndUpdateBooking(Long bookingId, LocalDate newEndDate, String paymentId,
			String orderId, String signature) {
		try {
			// 1. Create a JSONObject with ALL the payment details
			JSONObject options = new JSONObject();
			options.put("razorpay_order_id", orderId);
			options.put("razorpay_payment_id", paymentId);
			options.put("razorpay_signature", signature); // The signature goes INSIDE the JSON object

			// 2. Verify the signature using the JSON object and your secret key
			boolean isValid = Utils.verifyPaymentSignature(options, "SJav5iDCiTd3x43xN3U4ru1Z");

			if (!isValid) {
				throw new RazorpayException("Payment verification failed: Invalid signature.");
			}

			// 3. If verification is successful, update the booking's end date
			Booking booking = bookingRepository.findById(bookingId)
					.orElseThrow(() -> new RuntimeException("Booking not found"));

			booking.setEndDate(newEndDate);
			// You might also want to update the totalAmount here if you store it
			bookingRepository.save(booking);

		} catch (RazorpayException e) {
			// Handle Razorpay-specific exceptions
			logger.error("Razorpay verification failed", e);
			throw new RuntimeException("Payment verification failed.");
		} catch (Exception e) {
			// Handle other exceptions like org.json.JSONException
			logger.error("An error occurred during payment verification", e);
			throw new RuntimeException("An error occurred during payment verification.");
		}
	}
}
