package com.SpringProject.kharidoMat.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.model.Review;
import com.SpringProject.kharidoMat.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{itemId}")
    public ResponseEntity<Review> submitReview(@PathVariable Long itemId,
                                               @RequestBody Review review,
                                               Authentication authentication) {
        String email = authentication.getName();
        logger.info("User '{}' submitting review for item ID {}", email, itemId.toString());
        Review savedReview = reviewService.submitReview(itemId, email, review);
        logger.info("Review submitted successfully for item ID {}", itemId.toString());
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long itemId) {
        logger.info("Fetching reviews for item ID {}", itemId.toString());
        List<Review> reviews = reviewService.getReviewsForItem(itemId);
        return ResponseEntity.ok(reviews);
    }
}
