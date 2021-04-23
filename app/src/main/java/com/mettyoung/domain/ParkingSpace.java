package com.mettyoung.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParkingSpace {
    private static final String LOT_NAME_SUFFIX = "Lot";
    private static final Integer STARTING_LOT_NUMBER = 1;

    private Map<String, PriorityQueue<ParkingLot>> vacantLots = new HashMap<>();
    private Map<String, ParkingReservation> reservations = new HashMap<>();

    public void allocate(VehicleLots vehicleLots) {
        // Create a lot prefix (eg: CarLot, MotorcycleLot)
        var lotPrefix = vehicleLots.getVehicleType().getName() + LOT_NAME_SUFFIX;

        // Allocate parking lots given the vehicle type and number of lots
        var allocatedLots = IntStream.range(STARTING_LOT_NUMBER, vehicleLots.getNumberOfLots() + STARTING_LOT_NUMBER)
                .mapToObj(lotNumber -> new ParkingLot(lotPrefix, lotNumber))
                .collect(Collectors.toCollection(PriorityQueue::new));

        vacantLots.put(vehicleLots.getVehicleType().getName(), allocatedLots);
    }

    public ParkingReservation accept(Vehicle vehicle, LocalDateTime entryDateTime) {
        // Select vacant lots given vehicle type
        var selectedVacantLots = vacantLots.get(vehicle.getVehicleType().getName());

        // If no allocated lot, then the parking space does not support for this vehicle
        if (selectedVacantLots == null) {
            throw new InvalidVehicleException(vehicle);
        }
        // If no more vacant lots, then no reservation will be made
        else if (selectedVacantLots.isEmpty()) {
            return null;
        }

        // Dequeue the lowest available vacant lot at runtime O(log*n)
        var reservedLot = selectedVacantLots.remove();

        // Throws an error if the incoming vehicle is a duplicate
        if (reservations.containsKey(vehicle.getPlateNumber())) {
            throw new DuplicateVehicleException(vehicle);
        }

        // Create reservation
        var parkingReservation = new ParkingReservation(entryDateTime, reservedLot, vehicle);
        reservations.put(vehicle.getPlateNumber(), parkingReservation);
        return parkingReservation;
    }

    public ParkingReceipt release(Vehicle vehicle, LocalDateTime exitDateTime) {
        // Remove reservation
        ParkingReservation parkingReservation = reservations.remove(vehicle.getPlateNumber());
        if (parkingReservation == null) {
            return null;
        }

        // Return the parking lot to the selected pool
        var completeVehicle = parkingReservation.getVehicle();
        var selectedVacantLots = vacantLots.get(completeVehicle.getVehicleType().getName());
        selectedVacantLots.add(parkingReservation.getParkingLot());

        // Generate parking receipt
        var entryDateTime = parkingReservation.getEntryDateTime();
        var secondsSpent = ChronoUnit.SECONDS.between(entryDateTime, exitDateTime);
        var hoursSpent = BigDecimal.valueOf(Math.ceil(secondsSpent / 3600f));

        var parkingFee = completeVehicle.getVehicleType().getHourlyParkingRate().multiply(hoursSpent);
        return new ParkingReceipt(exitDateTime, parkingReservation, parkingFee);
    }
}
