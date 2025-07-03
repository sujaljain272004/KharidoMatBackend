package com.SpringProject.kharidoMat.service;

import java.util.List;

import com.SpringProject.kharidoMat.model.Review;

public interface ReviewService {
	Review submitReview(Long itemId, String email, Review review);
	List<Review> getReviewsForItem(Long itemId);

}
