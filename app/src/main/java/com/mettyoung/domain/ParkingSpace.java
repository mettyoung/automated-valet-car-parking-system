package com.mettyoung.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParkingSpace {
    private static final String LOT_NAME_SUFFIX = "Lot";
    private static final Integer STARTING_LOT_NUMBER = 1;

    private Map<String, List<ParkingLot>> vacantLots = new HashMap<>();

    public void allocate(VehicleLots vehicleLots) {
        var lots = IntStream.range(STARTING_LOT_NUMBER, vehicleLots.getNumberOfLots() + STARTING_LOT_NUMBER)
                .mapToObj(lotNumber -> new ParkingLot(vehicleLots.getVehicleType().getName() + LOT_NAME_SUFFIX + lotNumber))
                .collect(Collectors.toCollection(ArrayList::new));

        vacantLots.put(vehicleLots.getVehicleType().getName(), lots);
    }
}
