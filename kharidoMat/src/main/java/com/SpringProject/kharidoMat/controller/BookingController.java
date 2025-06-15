package com.SpringProject.kharidoMat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<?> bookItem(@PathVariable Long itemId,
                                      @RequestBody Booking bookingRequest,
                                      Authentication authentication) {
        String username = authentication.getName(); 
        Booking booking = bookingService.createBooking(itemId, username, bookingRequest);
        return ResponseEntity.ok(booking);
    }
}