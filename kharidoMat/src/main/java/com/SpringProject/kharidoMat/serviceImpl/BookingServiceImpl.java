package com.SpringProject.kharidoMat.serviceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    public Booking createBooking(Long itemId, String username, Booking bookingRequest) {
        logger.info("Creating booking for item {} by user {}", itemId, username);

        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            logger.error("Item not found for ID {}", itemId);
            throw new RuntimeException("Item not found");
        }
        Item item = itemOpt.get();

        User user = userRepository.findByEmail(username);
        if (user == null) {
            logger.error("User not found with email {}", username);
            throw new RuntimeException("User not found");
        }

        if (item.getUser().getEmail().equals(username)) {
            logger.warn("User {} attempted to book their own item", username);
            throw new RuntimeException("You cannot book your own item.");
        }

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                itemId, bookingRequest.getStartDate(), bookingRequest.getEndDate());

        if (!conflicts.isEmpty()) {
            logger.warn("Booking conflict for item {} between {} and {}", itemId, bookingRequest.getStartDate(), bookingRequest.getEndDate());
            throw new RuntimeException("Item already booked for the selected dates.");
        }

        long days = ChronoUnit.DAYS.between(bookingRequest.getStartDate(), bookingRequest.getEndDate());
        double pricePerDay = item.getPricePerDay();
        bookingRequest.setAmount(days * pricePerDay);
        bookingRequest.setItem(item);
        bookingRequest.setUser(user);
        bookingRequest.setStatus(BookingStatus.ACTIVE);

        logger.info("Booking created: itemId={}, user={}, amount={}", itemId, username, bookingRequest.getAmount());
        return bookingRepository.save(bookingRequest);
    }

    @Override
    public List<Booking> getBookingByUser(String username) {
        logger.info("Fetching bookings for user {}", username);

        User user = userRepository.findByEmail(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found");
        }

        return bookingRepository.findByUser(user);
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
                    + "This is a reminder to return the item: " + itemName
                    + " by tomorrow (" + booking.getEndDate() + ").\n\nThanks!\nCampusRent Team";

            emailService.sendEmail(to, "Return Reminder: " + itemName, body);
            logger.info("Reminder email sent to {} for item {}", to, itemName);
        }
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        logger.info("Fetching booking by ID: {}", bookingId);

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking not found: {}", bookingId);
                    return new RuntimeException("Booking not found");
                });
    }

    @Override
    public Booking cancelBooking(Long bookingId, String username) {
        logger.info("Cancel booking request by {} for booking {}", username, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
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

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
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

        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                booking.getItem().getId(), booking.getEndDate().plusDays(1), newEndDate);

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
}
