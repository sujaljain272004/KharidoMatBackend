package com.SpringProject.kharidoMat.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.dto.ReviewDto;
import com.SpringProject.kharidoMat.dto.UserDTO;
import com.SpringProject.kharidoMat.enums.BookingStatus;
import com.SpringProject.kharidoMat.model.Review;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.ReviewRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.ReviewService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Review submitReview(Long itemId, String email, Review review) {
        log.info("Submitting review for itemId: {}, by user: {}", itemId, email);
        

        var item = itemRepository.findById(itemId).orElseThrow();
        var user = userRepository.findByEmail(email);

        review.setItem(item);
        review.setUser(user);
        review.setLocalDateTime(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        log.info("Review submitted successfully with ID: {}", savedReview.getId());

        return savedReview;
    }

    @Override
    public List<Review> getReviewsForItems(Long itemId) {
        log.info("Fetching reviews for itemId: {}", itemId);
        return reviewRepository.findByItemId(itemId);
    }
    
    @Override
    public List<ReviewDto> getReviewsForItem(Long itemId) {
        log.info("Fetching reviews for itemId: {}", itemId);

        // 1. Get the original list of reviews from the database.
        List<Review> reviews = reviewRepository.findByItemId(itemId);

        // 2. Create a new, empty list for our DTOs.
        List<ReviewDto> reviewDtos = new ArrayList<>();

        // 3. Loop through each original 'Review' entity.
        for (Review review : reviews) {
            // Convert the entity to a DTO and add it to our new list.
            reviewDtos.add(convertToDto(review));
        }

        // 4. Return the new list filled with DTOs.
        return reviewDtos;
    }
    
 // This helper method is still used and does not need to change.
    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setLocalDateTime(review.getLocalDateTime());

        if (review.getUser() != null) {
            dto.setUser(new UserDTO(review.getUser().getFullName()));
        }

        return dto;
    }
    
 // In src/main/java/com/yourproject/serviceImpl/ReviewServiceImpl.java

    @Override
    public boolean canUserReviewItem(Long itemId, String email) {
        // 1. Check if the user has a completed booking for this item.
        boolean hasCompletedBooking = bookingRepository.findAllByUserEmail(email)
                .stream()
                .anyMatch(booking -> 
                    booking.getItem().getId().equals(itemId) && 
                    booking.getStatus() == BookingStatus.COMPLETED // <-- CORRECTED LINE
                );

        if (!hasCompletedBooking) {
            return false; // If no completed booking, they can't review.
        }

        // 2. Check if the user has already reviewed this item.
        boolean hasAlreadyReviewed = reviewRepository.findByItemId(itemId)
                .stream()
                .anyMatch(review -> review.getUser().getEmail().equals(email));

        // They can review only if they have a completed booking AND have not already reviewed.
        return hasCompletedBooking && !hasAlreadyReviewed;
    }
}
