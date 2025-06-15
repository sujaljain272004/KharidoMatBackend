package com.SpringProject.kharidoMat.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SpringProject.kharidoMat.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	@Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
		       "AND (:startDate <= b.endDate AND :endDate >= b.startDate)")
		List<Booking> findConflictingBookings(@Param("itemId") Long itemId,
		                                      @Param("startDate") LocalDate startDate,
		                                      @Param("endDate") LocalDate endDate);

}