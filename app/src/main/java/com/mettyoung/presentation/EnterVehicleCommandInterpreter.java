package com.mettyoung.presentation;

import com.mettyoung.application.EnterVehicleCommand;
import com.mettyoung.domain.ParkingReservation;
import com.mettyoung.domain.ParkingSpace;
import com.mettyoung.domain.Vehicle;
import com.mettyoung.domain.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
public class EnterVehicleCommandInterpreter extends AbstractParsingInterpreter {

    private final ParkingSpace parkingSpace;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public String getCommand() {
        return "ENTER";
    }

    @Override
    public int getNumberOfArguments() {
        return 3;
    }

    @Override
    public boolean interpret(String[] args) {
        // Parsing logic
        var vehicleType = vehicleTypeRepository.findByName(args[0]).orElse(null);
        if (vehicleType == null || StringUtils.isBlank(args[1]) || !NumberUtils.isParsable(args[2])) {
            return false;
        }

        var timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(Integer.parseInt(args[2])),
                ZoneId.systemDefault()
        );

        // Invoke application core and print response
        System.out.print(reconstructOriginalCommand(args) + " -> ");

        try {
            var enterVehicleCommand = new EnterVehicleCommand(parkingSpace, new Vehicle(vehicleType, args[1]), timestamp);
            enterVehicleCommand.setOnSuccessHandler(this::onSuccess);
            enterVehicleCommand.setOnFailureHandler(this::onFailure);
            enterVehicleCommand.execute();
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
        return true;
    }

    private void onSuccess(ParkingReservation parkingReservation) {
        System.out.printf(
                "Accepted at %s: %s\n",
                parkingReservation.getEntryDateTime(),
                parkingReservation.getParkingLot().getName()
        );
    }

    private void onFailure(Vehicle vehicle) {
        System.out.printf(
                "Entry failed: no available lot for %s %s \n",
                vehicle.getVehicleType().getName(),
                vehicle.getPlateNumber()
        );
    }
}
