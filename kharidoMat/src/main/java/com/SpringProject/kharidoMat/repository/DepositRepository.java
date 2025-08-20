package com.SpringProject.kharidoMat.repository;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.Deposit;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
	Optional<Deposit> findByBooking(Booking booking);
}
