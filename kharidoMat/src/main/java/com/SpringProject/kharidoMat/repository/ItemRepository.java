package com.SpringProject.kharidoMat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;

public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findByCategoryIgnoreCase(String category);
	
	 @Query("SELECT i FROM Item i WHERE " +
	            "(:title IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
	            "(:category IS NULL OR LOWER(i.category) = LOWER(:category)) AND " +
	            "(:minPrice IS NULL OR i.pricePerDay >= :minPrice) AND " +
	            "(:maxPrice IS NULL OR i.pricePerDay <= :maxPrice) AND " +
	            "(:available IS NULL OR i.available = :available)")
	    List<Item> searchItems(@Param("title") String title,
	                           @Param("category") String category,
	                           @Param("minPrice") Double minPrice,
	                           @Param("maxPrice") Double maxPrice,
	                           @Param("available") Boolean available);
	 
	 @Query("SELECT COUNT(i) FROM Item i WHERE i.user.id = :ownerId")
	 int countByOwnerId(@Param("ownerId") Long ownerId);
	 
	 List<Item> findByUser(User user);
	 @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.bookings WHERE i.user.email = :email")
	 List<Item> findItemsWithBookingsByUserEmail(@Param("email") String email);


	 
}