package com.mettyoung.application;

import com.mettyoung.domain.ParkingReservation;
import com.mettyoung.domain.ParkingSpace;
import com.mettyoung.domain.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class EnterVehicleCommand {
    private final ParkingSpace parkingSpace;
    private final Vehicle vehicle;
    private final LocalDateTime timestamp;

    @Setter
    private Consumer<ParkingReservation> onSuccessHandler;

    @Setter
    private Consumer<Vehicle> onFailureHandler;

    public void execute() {
        var parkingReservation = parkingSpace.accept(vehicle, timestamp);
        if (parkingReservation == null) {
            onFailureHandler.accept(vehicle);
        } else {
            onSuccessHandler.accept(parkingReservation);
        }
    }
}
