package com.mettyoung.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Vehicle {
    VehicleType vehicleType;
    String plateNumber;

    public Vehicle(String plateNumber) {
        this(null, plateNumber);
    }
}
