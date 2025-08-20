package com.SpringProject.kharidoMat.service;

import com.SpringProject.kharidoMat.model.Deposit;

public interface DepositService {
    Deposit createDeposit(Double amount, Long bookingId);
}
