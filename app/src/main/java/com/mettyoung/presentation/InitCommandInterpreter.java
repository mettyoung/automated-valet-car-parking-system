package com.mettyoung.presentation;

import com.mettyoung.domain.ParkingSpace;
import com.mettyoung.domain.VehicleLots;
import com.mettyoung.domain.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

@RequiredArgsConstructor
public class InitCommandInterpreter extends AbstractParsingInterpreter {

    private final ParkingSpace parkingSpace;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    public String getCommand() {
        return "INIT";
    }

    @Override
    public int getNumberOfArguments() {
        return 2;
    }

    @Override
    public boolean interpret(String[] args) {
        // Parsing logic
        var vehicleType = vehicleTypeRepository.findByName(args[0]).orElse(null);
        if (vehicleType == null || !NumberUtils.isParsable(args[1])) {
            return false;
        }
        var numberOfLots = Integer.parseInt(args[1]);

        // Invoke application core
        parkingSpace.allocate(new VehicleLots(vehicleType, numberOfLots));

        // Print response
        System.out.printf("%s -> Allocated %d lots for %s\n",
                reconstructOriginalCommand(args),
                numberOfLots,
                vehicleType.getName()
        );
        return true;
    }
}
