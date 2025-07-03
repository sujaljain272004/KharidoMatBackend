package com.SpringProject.kharidoMat.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import org.ietf.jgss.Oid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(username);

        if (itemOpt.isEmpty()) throw new RuntimeException("Item not found");

        bookingRequest.setItem(itemOpt.get());
        bookingRequest.setUser(user);
        
  
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            itemId, bookingRequest.getStartDate(), bookingRequest.getEndDate()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Item already booked for selected dates.");
        }


        return bookingRepository.save(bookingRequest);
    }

	@Override
	public List<Booking> getBookingByUser(String username) {
		
		User user = userRepository.findByEmail(username);
		if(user == null) {
			 throw new UsernameNotFoundException("User not found");
		}
		return bookingRepository.findByUser(user);
	}

	@Override
	public List<Booking> getBookingsForOwner(String username) {
		
		User user = userRepository.findByEmail(username);
		if(user == null) {
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
	        String body = "Dear " + booking.getUser().getFullName() +
	            ",\n\nThis is a reminder to return the item: " + item +
	            " by tomorrow (" + booking.getEndDate() + ").\n\nThanks!\nCampusRent Team";

	        emailService.sendEmail(to, "Return Reminder: " + item, body);
	    }
	}
}


