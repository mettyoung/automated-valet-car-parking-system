package com.mettyoung.domain;

import lombok.Value;

@Value
public class ParkingLot implements Comparable<ParkingLot> {
    String prefix;
    Integer lotNumber;

    public String getName() {
        return prefix + lotNumber;
    }

    @Override
    public int compareTo(ParkingLot other) {
        return Integer.compare(lotNumber, other.lotNumber);
    }
}
