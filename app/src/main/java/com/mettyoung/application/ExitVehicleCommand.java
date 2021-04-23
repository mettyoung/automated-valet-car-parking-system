package com.mettyoung.application;

import com.mettyoung.domain.ParkingReceipt;
import com.mettyoung.domain.ParkingSpace;
import com.mettyoung.domain.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ExitVehicleCommand {
    private final ParkingSpace parkingSpace;
    private final Vehicle vehicle;
    private final LocalDateTime timestamp;

    @Setter
    private Consumer<ParkingReceipt> onSuccessHandler;

    @Setter
    private Consumer<Vehicle> onFailureHandler;

    public void execute() {
        var parkingReceipt = parkingSpace.release(vehicle, timestamp);
        if (parkingReceipt == null) {
            onFailureHandler.accept(vehicle);
        } else {
            onSuccessHandler.accept(parkingReceipt);
        }
    }
}
