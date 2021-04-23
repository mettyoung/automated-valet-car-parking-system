package com.mettyoung.presentation;

import com.mettyoung.domain.ParkingSpace;
import com.mettyoung.persistence.InMemoryVehicleTypeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConsoleApplication {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Error: Please specify input filename");
        }

        var parkingSpace = new ParkingSpace();
        var vehicleTypeRepository = new InMemoryVehicleTypeRepository();

        var consoleInputInterpreter = new ConsoleInputInterpreter()
                .addInterpreter(new InitCommandInterpreter(parkingSpace, vehicleTypeRepository))
                .addInterpreter(new EnterVehicleCommandInterpreter(parkingSpace, vehicleTypeRepository))
                .addInterpreter(new ExitVehicleCommandInterpreter(parkingSpace))
                .addInterpreter(new DefaultInterpreter());

        Files.lines(Path.of(args[0])).forEach(consoleInputInterpreter::interpret);
    }
}