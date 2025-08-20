package com.SpringProject.kharidoMat.model;

import com.SpringProject.kharidoMat.enums.DepositStatus;
import jakarta.persistence.*;

@Entity
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The deposit amount
    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositStatus status;

    // Link to the booking this deposit belongs to
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    public Deposit() {}

    public Deposit(Double amount, DepositStatus status, Booking booking) {
        this.amount = amount;
        this.status = status;
        this.booking = booking;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public DepositStatus getStatus() {
        return status;
    }

    public void setStatus(DepositStatus status) {
        this.status = status;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
