package com.SpringProject.kharidoMat.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Override
	public Booking createBooking(Long itemId, String username, Booking bookingRequest) {

		Optional<Item> itemOpt = itemRepository.findById(itemId);
		if (itemOpt.isEmpty()) {
			throw new RuntimeException("Item not found");
		}
		Item item = itemOpt.get();

		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new RuntimeException("User not found");
		}

		if (item.getUser().getEmail().equals(username)) {
			throw new RuntimeException("You cannot book your own item.");
		}

		List<Booking> conflicts = bookingRepository.findConflictingBookings(itemId, bookingRequest.getStartDate(),
				bookingRequest.getEndDate());

		if (!conflicts.isEmpty()) {
			throw new RuntimeException("Item already booked for the selected dates.");
		}

		bookingRequest.setItem(item);
		bookingRequest.setUser(user);
		bookingRequest.setStatus(BookingStatus.ACTIVE);
		return bookingRepository.save(bookingRequest);
	}

	@Override
	public List<Booking> getBookingByUser(String username) {

		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return bookingRepository.findByUser(user);
	}

	@Override
	public List<Booking> getBookingsForOwner(String username) {

		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}

		return bookingRepository.findBookingsByItemOwner(user);
	}

	@Scheduled(cron = "0 0 8 * * ?") // Every day at 8 AM
	public void sendReminderEmails() {
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		List<Booking> bookings = bookingRepository.findByEndDate(tomorrow);

		for (Booking booking : bookings) {
			String to = booking.getUser().getEmail();
			String item = booking.getUser().getFullName();
			String body = "Dear " + booking.getUser().getFullName() + ",\n\nThis is a reminder to return the item: "
					+ item + " by tomorrow (" + booking.getEndDate() + ").\n\nThanks!\nCampusRent Team";

			emailService.sendEmail(to, "Return Reminder: " + item, body);
		}
	}

	@Override
	public Booking cancelBooking(Long bookingId, String username) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (!booking.getUser().getEmail().equals(username)) {
			throw new RuntimeException("You are not allowed to cancel this booking");
		}

		if (LocalDate.now().isAfter(booking.getStartDate())) {
			throw new RuntimeException("Cannot cancel booking after start date");
		}

		booking.setStatus(BookingStatus.CANCELED);
		return bookingRepository.save(booking);
	}

	@Override
	public Booking extendBooking(Long bookingId, LocalDate newEndDate, String username) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		if (!booking.getUser().getEmail().equals(username)) {
			throw new RuntimeException("Unauthorized");
		}

		if (booking.getStatus() != BookingStatus.ACTIVE) {
			throw new RuntimeException("Only ACTIVE bookings can be extended");
		}

		if (!newEndDate.isAfter(booking.getEndDate())) {
			throw new RuntimeException("New end date must be after current end date");
		}

		List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(booking.getItem().getId(),
				booking.getEndDate().plusDays(1), newEndDate);

		if (!conflictingBookings.isEmpty()) {
			throw new RuntimeException("Item is already booked during this extension period");
		}

		booking.setEndDate(newEndDate);
		return bookingRepository.save(booking);
	}

	@Override
	public Map<String, List<Booking>> getBookingsGroupedByStatus(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("User not found");
		}

		LocalDate today = LocalDate.now();

		Map<String, List<Booking>> result = new HashMap<>();
		result.put("upcoming", bookingRepository.findUpcomingBookings(user, today));
		result.put("ongoing", bookingRepository.findOngoingBookings(user, today));
		result.put("past", bookingRepository.findPastBookings(user, today));

		return result;
	}

}
