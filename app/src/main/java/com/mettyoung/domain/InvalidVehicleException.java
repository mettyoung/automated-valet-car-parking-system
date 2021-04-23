package com.mettyoung.domain;

import lombok.Value;

@Value
public class InvalidVehicleException extends RuntimeException {
    Vehicle vehicle;

    public InvalidVehicleException(Vehicle vehicle) {
        super("Invalid vehicle - " + vehicle.getPlateNumber());
        this.vehicle = vehicle;
    }
}
