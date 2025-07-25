package com.SpringProject.kharidoMat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SpringProject.kharidoMat.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
	List<Review> findByItemId(Long itemId);
	
	/**
     * Calculates the average rating for a given item.
     * Returns a Double, which can be null if there are no reviews.
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId")
    Double findAverageRatingByItemId(@Param("itemId") Long itemId);

    /**
     * Counts the total number of reviews for a given item.
     * Returns a Long.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.item.id = :itemId")
    Long countReviewsByItemId(@Param("itemId") Long itemId);
}
