package com.mettyoung.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class ParkingReceipt {
    LocalDateTime exitDateTime;
    ParkingReservation parkingReservation;
    BigDecimal parkingFee;
}
