package com.mettyoung.presentation;

import com.mettyoung.application.ExitVehicleCommand;
import com.mettyoung.domain.ParkingReceipt;
import com.mettyoung.domain.ParkingSpace;
import com.mettyoung.domain.Vehicle;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
public class ExitVehicleCommandInterpreter extends AbstractParsingInterpreter {

    private final ParkingSpace parkingSpace;

    @Override
    public String getCommand() {
        return "EXIT";
    }

    @Override
    public int getNumberOfArguments() {
        return 2;
    }

    @Override
    public boolean interpret(String[] args) {
        // Parsing logic
        if (StringUtils.isBlank(args[0]) || !NumberUtils.isParsable(args[1])) {
            return false;
        }

        var timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(Integer.parseInt(args[1])),
                ZoneId.systemDefault()
        );

        // Invoke application core and print response
        System.out.print(reconstructOriginalCommand(args) + " -> ");
        var exitVehicleCommand = new ExitVehicleCommand(parkingSpace, new Vehicle(args[0]), timestamp);
        exitVehicleCommand.setOnSuccessHandler(this::onSuccess);
        exitVehicleCommand.setOnFailureHandler(this::onFailure);
        exitVehicleCommand.execute();
        return true;
    }

    private void onSuccess(ParkingReceipt parkingReceipt) {
        System.out.printf(
                "%s released at %s: $%.2f charged for $%.2f/hour\n",
                parkingReceipt.getParkingReservation().getParkingLot().getName(),
                parkingReceipt.getExitDateTime(),
                parkingReceipt.getParkingFee(),
                parkingReceipt.getParkingReservation().getVehicle().getVehicleType().getHourlyParkingRate()
        );
    }

    private void onFailure(Vehicle vehicle) {
        System.out.printf(
                "Exit failed: %s not found\n",
                vehicle.getPlateNumber()
        );
    }
}
