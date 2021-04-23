package com.mettyoung.domain;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ParkingReservation {
    LocalDateTime entryDateTime;
    ParkingLot parkingLot;
    Vehicle vehicle;
}
