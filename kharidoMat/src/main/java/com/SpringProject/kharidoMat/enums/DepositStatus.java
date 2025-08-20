package com.SpringProject.kharidoMat.enums;

public enum DepositStatus {
    HELD,       // Collected and waiting (booking ongoing or pending return)
    REFUNDED,   // Returned to renter
    FORFEITED   // Kept due to damages / policy violation
}