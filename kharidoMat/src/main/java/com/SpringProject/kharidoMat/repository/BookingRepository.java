package com.SpringProject.kharidoMat.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);
    
    List<Booking> findByUserId(Long userId);

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

    int countByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.user.id = :userId")
    double sumAmountSpentByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.item.user.id = :ownerId")
    double sumAmountEarnedByOwnerId(@Param("ownerId") Long ownerId);
    
    List<Booking> findAllByItemId(Long itemId);

}
