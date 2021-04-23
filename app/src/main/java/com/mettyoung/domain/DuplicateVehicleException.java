package com.mettyoung.domain;

import lombok.Value;

@Value
public class DuplicateVehicleException extends RuntimeException {
    Vehicle vehicle;

    public DuplicateVehicleException(Vehicle vehicle) {
        super("Duplicate vehicle - " + vehicle.getPlateNumber());
        this.vehicle = vehicle;
    }
}
