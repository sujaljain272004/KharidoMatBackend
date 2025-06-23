package com.SpringProject.kharidoMat.serviceImpl;

import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

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
	
	
}