package com.mettyoung.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public ParkingReservation accept(Vehicle vehicle, LocalDateTime entryDateTime) {
        var vehicleVacantLots = vacantLots.get(vehicle.getVehicleType().getName());
        if (vehicleVacantLots == null) {
            throw new InvalidVehicleException(vehicle);
        }
        else if (vehicleVacantLots.isEmpty()) {
            return null;
        }

        var reservedLot = vehicleVacantLots.remove(0);
        var parkingReservation =  new ParkingReservation(entryDateTime, reservedLot, vehicle);
        if (reservations.containsKey(vehicle.getPlateNumber())) {
            throw new DuplicateVehicleException(vehicle);
        }
        reservations.put(vehicle.getPlateNumber(), parkingReservation);

        return parkingReservation;
    }

    public ParkingReceipt release(Vehicle vehicle, LocalDateTime exitDateTime) {
        // Remove reservation
        ParkingReservation parkingReservation = reservations.remove(vehicle.getPlateNumber());
        if (parkingReservation == null) {
            throw new VehicleNotFoundException(vehicle);
        }

        // Return the parking lot to the selected pool
        var selectedVacantLots = vacantLots.get(vehicle.getVehicleType().getName());
        selectedVacantLots.add(parkingReservation.getParkingLot());

        // Generate parking receipt
        var entryDateTime = parkingReservation.getEntryDateTime();
        var secondsSpent = ChronoUnit.SECONDS.between(entryDateTime, exitDateTime);
        var hoursSpent = BigDecimal.valueOf(Math.ceil(secondsSpent / 3600f));

        var parkingFee = vehicle.getVehicleType().getHourlyParkingRate().multiply(hoursSpent);
        return new ParkingReceipt(exitDateTime, parkingReservation, parkingFee);
    }
}
