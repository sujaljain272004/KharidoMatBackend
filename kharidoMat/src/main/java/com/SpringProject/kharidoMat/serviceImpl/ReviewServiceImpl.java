package com.SpringProject.kharidoMat.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.model.Review;
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
    public List<Review> getReviewsForItem(Long itemId) {
        log.info("Fetching reviews for itemId: {}", itemId);
        return reviewRepository.findByItemId(itemId);
    }
}
