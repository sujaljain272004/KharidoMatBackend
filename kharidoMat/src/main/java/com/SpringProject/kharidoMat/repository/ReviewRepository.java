package com.SpringProject.kharidoMat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SpringProject.kharidoMat.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
	List<Review> findByItemId(Long itemId);
}
