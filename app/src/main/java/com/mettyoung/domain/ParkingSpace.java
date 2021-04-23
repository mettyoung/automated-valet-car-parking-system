package com.mettyoung.domain;

import java.time.LocalDateTime;
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
    private Map<String, ParkingReservation> reservations = new HashMap<>();

    public void allocate(VehicleLots vehicleLots) {
        var allocatedLots = IntStream.range(STARTING_LOT_NUMBER, vehicleLots.getNumberOfLots() + STARTING_LOT_NUMBER)
                .mapToObj(lotNumber -> new ParkingLot(vehicleLots.getVehicleType().getName() + LOT_NAME_SUFFIX + lotNumber))
                .collect(Collectors.toCollection(ArrayList::new));

        vacantLots.put(vehicleLots.getVehicleType().getName(), allocatedLots);
    }

    public ParkingReservation accept(Vehicle vehicle, LocalDateTime timestamp) {
        var vehicleVacantLots = vacantLots.get(vehicle.getVehicleType().getName());
        if (vehicleVacantLots == null) {
            throw new InvalidVehicleException(vehicle);
        }
        else if (vehicleVacantLots.isEmpty()) {
            return null;
        }

        var reservedLot = vehicleVacantLots.remove(0);
        var parkingReservation =  new ParkingReservation(timestamp, reservedLot, vehicle);
        if (reservations.containsKey(vehicle.getPlateNumber())) {
            throw new DuplicateVehicleException(vehicle);
        }
        reservations.put(vehicle.getPlateNumber(), parkingReservation);

        return parkingReservation;
    }
}
