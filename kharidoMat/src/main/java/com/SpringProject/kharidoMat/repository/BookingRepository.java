package com.SpringProject.kharidoMat.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    @Query("SELECT b FROM Booking b WHERE b.item.user = :owner")
    List<Booking> findBookingsByItemOwner(@Param("owner") User owner);

    List<Booking> findByEndDate(LocalDate endDate);

    List<Booking> findByReturnStatus(String returnStatus);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.item.id = :itemId " +
           "AND b.status = 'ACTIVE' " +
           "AND (:startDate <= b.endDate AND :endDate >= b.startDate)")
    List<Booking> findConflictingBookings(@Param("itemId") Long itemId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = 'ACTIVE' AND b.startDate > :today")
    List<Booking> findUpcomingBookings(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = 'ACTIVE' AND :today BETWEEN b.startDate AND b.endDate")
    List<Booking> findOngoingBookings(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = 'ACTIVE' AND b.endDate < :today")
    List<Booking> findPastBookings(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(i.pricePerDay) FROM Booking b JOIN b.item i WHERE b.user.id = :userId AND b.status = 'APPROVED'")
    Double getTotalSpentByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(i.pricePerDay) FROM Booking b JOIN b.item i WHERE i.user.id = :ownerId AND b.status = 'APPROVED'")
    Double getTotalEarningsByOwner(@Param("ownerId") Long ownerId);
}
