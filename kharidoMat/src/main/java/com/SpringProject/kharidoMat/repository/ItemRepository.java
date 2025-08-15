package com.SpringProject.kharidoMat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;

import jakarta.persistence.LockModeType;

public interface ItemRepository extends JpaRepository<Item, Long> {

	@Query("SELECT i FROM Item i JOIN i.categories c WHERE LOWER(c.name) = LOWER(:categoryName)")
	List<Item> findByCategoryNameIgnoreCase(@Param("categoryName") String categoryName);

	
	 @Query("SELECT i FROM Item i WHERE " +
	            "(:title IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
	            "(:category IS NULL OR EXISTS (SELECT c FROM i.categories c WHERE LOWER(c.name) = LOWER(:category))) AND "+
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
	 
	 @Lock(LockModeType.PESSIMISTIC_WRITE)
	 @Query("SELECT i FROM Item i WHERE i.id = :itemId")
	 Optional<Item> findByIdForUpdate(@Param("itemId") Long itemId);

	 
}