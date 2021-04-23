package com.mettyoung.domain;

import lombok.Value;

@Value
public class VehicleNotFoundException extends RuntimeException {
    Vehicle vehicle;

    public VehicleNotFoundException(Vehicle vehicle) {
        super("Vehicle not found - " + vehicle.getPlateNumber());
        this.vehicle = vehicle;
    }
}
