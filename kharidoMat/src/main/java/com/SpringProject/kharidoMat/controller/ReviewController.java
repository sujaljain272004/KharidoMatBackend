package com.SpringProject.kharidoMat.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.dto.ReviewDto;
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
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long itemId) {
        List<ReviewDto> reviews = reviewService.getReviewsForItem(itemId);
        return ResponseEntity.ok(reviews);
    }
    
 // --- ADD THIS ENTIRE METHOD ---
    @GetMapping("/can-review/{itemId}")
    public ResponseEntity<Map<String, Boolean>> canUserReview(@PathVariable Long itemId, Authentication authentication) {
        String email = authentication.getName();
        boolean canReview = reviewService.canUserReviewItem(itemId, email);
        // Return a simple JSON object: { "canReview": true } or { "canReview": false }
        return ResponseEntity.ok(Map.of("canReview", canReview));
    }
}
