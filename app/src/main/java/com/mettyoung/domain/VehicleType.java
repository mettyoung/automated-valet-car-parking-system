package com.mettyoung.domain;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class VehicleType {
    String name;
    BigDecimal hourlyParkingRate;
}
