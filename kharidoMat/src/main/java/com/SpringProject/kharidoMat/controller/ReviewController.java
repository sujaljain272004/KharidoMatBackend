package com.SpringProject.kharidoMat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringProject.kharidoMat.model.Review;
import com.SpringProject.kharidoMat.service.ReviewService;

//Added review System
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	
	@Autowired
	private ReviewService reviewService;
	
	@PostMapping("/{itemId}")
	public ResponseEntity<Review> submitReview(@PathVariable Long itemId, @RequestBody Review review, Authentication authentication)
	{
		String email = authentication.getName();
		return ResponseEntity.ok(reviewService.submitReview(itemId, email, review));
	}
	
	@GetMapping("/{itemId}")
	public ResponseEntity<List<Review>> getReviews(@PathVariable Long itemId)
	{
		return ResponseEntity.ok(reviewService.getReviewsForItem(itemId));
	}
 }
