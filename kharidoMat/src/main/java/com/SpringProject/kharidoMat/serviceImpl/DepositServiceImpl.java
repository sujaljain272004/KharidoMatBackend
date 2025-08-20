package com.SpringProject.kharidoMat.serviceImpl;

import com.SpringProject.kharidoMat.enums.DepositStatus;
import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.Deposit;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.DepositRepository;
import com.SpringProject.kharidoMat.service.DepositService;
import org.springframework.stereotype.Service;

@Service
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final BookingRepository bookingRepository;

    public DepositServiceImpl(DepositRepository depositRepository, BookingRepository bookingRepository) {
        this.depositRepository = depositRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Deposit createDeposit(Double amount, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Deposit deposit = new Deposit();
        deposit.setAmount(amount);
        deposit.setStatus(DepositStatus.HELD);
        deposit.setBooking(booking);

        return depositRepository.save(deposit);
    }
}
