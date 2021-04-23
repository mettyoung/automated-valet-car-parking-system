package com.mettyoung.domain

import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ParkingSpaceSpec extends Specification {

    final CAR_TYPE_NAME = "Car"
    final CAR_HOURLY_RATE = 2 as BigDecimal
    final CAR_TYPE = new VehicleType(CAR_TYPE_NAME, CAR_HOURLY_RATE)

    final MOTORCYCLE_TYPE_NAME = "Motorcycle"
    final MOTORCYCLE_HOURLY_RATE = 1 as BigDecimal
    final MOTORCYCLE_TYPE = new VehicleType(MOTORCYCLE_TYPE_NAME, MOTORCYCLE_HOURLY_RATE)

    final LOCAL_DATE = LocalDate.of(2021, 4, 23)

    //region Allocate Parking Lots
    def "should be able to allocate parking lots given car and motorcycle lots"() {
        given:
        def carLots = new VehicleLots(CAR_TYPE, 10)
        def motorcycleLots = new VehicleLots(MOTORCYCLE_TYPE, 5)

        when:
        def parkingSpace = new ParkingSpace()
        parkingSpace.allocate(carLots)
        parkingSpace.allocate(motorcycleLots)

        then:
        for (vehicleLots in [carLots, motorcycleLots]) {
            def parkingLots = parkingSpace.vacantLots[vehicleLots.vehicleType.name]
            assert parkingLots.size == vehicleLots.numberOfLots

            def i = 0
            for (parkingLot in parkingLots) {
                assert parkingLot.name == "${vehicleLots.vehicleType.name}Lot${++i}"
            }
        }
    }
    //endregion

    //region Accept Vehicle
    def "should be able to accept car and motorcycle if slots are available"() {
        given:
        def parkingSpace = new ParkingSpace()
        parkingSpace.allocate(new VehicleLots(CAR_TYPE, 10))
        parkingSpace.allocate(new VehicleLots(MOTORCYCLE_TYPE, 10))

        when:
        def carReservation = parkingSpace.accept(
                new Vehicle(CAR_TYPE, "STR1234"),
                LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
        )
        def motorcycleReservation = parkingSpace.accept(
                new Vehicle(MOTORCYCLE_TYPE, "STR1235"),
                LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
        )

        then:
        parkingSpace.vacantLots[CAR_TYPE_NAME].size == 9
        with(carReservation) {
            entryDateTime == LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
            parkingLot.name == "CarLot1"
            vehicle.plateNumber == "STR1234"
            vehicle.vehicleType == CAR_TYPE
        }
        with(motorcycleReservation) {
            entryDateTime == LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
            parkingLot.name == "MotorcycleLot1"
            vehicle.plateNumber == "STR1235"
            vehicle.vehicleType == MOTORCYCLE_TYPE
        }
    }

    def "should not accept car if no more slots available"() {
        given:
        def parkingSpace = new ParkingSpace()
        parkingSpace.allocate(new VehicleLots(CAR_TYPE, 1))
        def firstReservation = parkingSpace.accept(
                new Vehicle(CAR_TYPE, "STR1234"),
                LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
        )

        when:
        def lastReservation = parkingSpace.accept(
                new Vehicle(CAR_TYPE, "STR1235"),
                LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
        )

        then:
        parkingSpace.vacantLots[CAR_TYPE_NAME].size == 0
        firstReservation != null
        lastReservation == null
    }

    def "should not accept invalid vehicle type"() {
        given:
        def parkingSpace = new ParkingSpace()

        when:
        parkingSpace.accept(
            new Vehicle(CAR_TYPE, "STR1234"),
            LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
        )

        then:
        def exception = thrown(InvalidVehicleException)
        exception.vehicle.vehicleType == CAR_TYPE
        exception.vehicle.plateNumber == "STR1234"
    }

    def "should not accept duplicate vehicle even if different vehicle types"() {
        given:
        def parkingSpace = new ParkingSpace()
        parkingSpace.allocate(new VehicleLots(CAR_TYPE, 2))
        parkingSpace.allocate(new VehicleLots(MOTORCYCLE_TYPE, 2))
        def firstReservation = parkingSpace.accept(
                new Vehicle(CAR_TYPE, "STR1234"),
                LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
        )

        when:
        def lastReservation = parkingSpace.accept(
                new Vehicle(MOTORCYCLE_TYPE, "STR1234"),
                LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0))
        )

        then:
        def exception = thrown(DuplicateVehicleException)
        exception.vehicle.vehicleType == MOTORCYCLE_TYPE
        exception.vehicle.plateNumber == "STR1234"
    }
    //endregion

    //region Release Vehicle
    def "should be able to release car and motorcycle if vehicles are found"() {
        given:
        def parkingSpace = new ParkingSpace()
        parkingSpace.allocate(new VehicleLots(CAR_TYPE, 10))
        parkingSpace.allocate(new VehicleLots(MOTORCYCLE_TYPE, 10))

        def car = new Vehicle(CAR_TYPE, "STR1234")
        def motorcycle = new Vehicle(MOTORCYCLE_TYPE, "STR1235")

        def carReservation = parkingSpace.accept(car, LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0)))
        def motorcycleReservation = parkingSpace.accept(motorcycle, LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0)))

        when:
        def carReceipt = parkingSpace.release(car, LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0, 1)))
        def motorcycleReceipt = parkingSpace.release(motorcycle, LocalDateTime.of(LOCAL_DATE, LocalTime.of(15, 0, 1)))

        then:
        with(carReceipt) {
            parkingFee == 1 * CAR_HOURLY_RATE
            exitDateTime == LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0, 1))
            parkingReservation == carReservation
        }
        parkingSpace.vacantLots[CAR_TYPE_NAME].size == 10
        parkingSpace.vacantLots[CAR_TYPE_NAME].stream().filter(parkingLot -> parkingLot.name == "CarLot1").any()

        with(motorcycleReceipt) {
            parkingFee == 2 * MOTORCYCLE_HOURLY_RATE
            exitDateTime == LocalDateTime.of(LOCAL_DATE, LocalTime.of(15, 0, 1))
            parkingReservation == motorcycleReservation
        }
        parkingSpace.vacantLots[MOTORCYCLE_TYPE_NAME].size == 10
        parkingSpace.vacantLots[MOTORCYCLE_TYPE_NAME].stream().filter(parkingLot -> parkingLot.name == "MotorcycleLot1").any()
    }

    def "should print no receipt if vehicle is not found when releasing"() {
        given:
        def parkingSpace = new ParkingSpace()
        parkingSpace.allocate(new VehicleLots(CAR_TYPE, 10))

        when:
        def car = new Vehicle(CAR_TYPE, "STR1234")
        def parkingReceipt = parkingSpace.release(car, LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0, 1)))

        then:
        parkingReceipt == null
    }
    //endregion

    //region Complex Scenarios
    def "should reserve the lowest available lot"() {
        given:
        def parkingSpace = new ParkingSpace()
        parkingSpace.allocate(new VehicleLots(CAR_TYPE, 10))

        def cars = [
                new Vehicle(CAR_TYPE, "STR1234"),
                new Vehicle(CAR_TYPE, "STR1235"),
                new Vehicle(CAR_TYPE, "STR1236"),
                new Vehicle(CAR_TYPE, "STR1237")
        ]

        parkingSpace.accept(cars[0], LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0)))
        parkingSpace.accept(cars[1], LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0)))
        parkingSpace.accept(cars[2], LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0)))
        parkingSpace.accept(cars[3], LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 0)))
        parkingSpace.release(cars[3], LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 1)))
        parkingSpace.release(cars[1], LocalDateTime.of(LOCAL_DATE, LocalTime.of(14, 4)))

        when:
        def newReservation = parkingSpace.accept(cars[1], LocalDateTime.of(LOCAL_DATE, LocalTime.of(15, 0)))

        then:
        parkingSpace.vacantLots[CAR_TYPE_NAME].size == 7
        with(newReservation) {
            entryDateTime == LocalDateTime.of(LOCAL_DATE, LocalTime.of(15, 0))
            parkingLot.name == "CarLot2"
            vehicle.plateNumber == "STR1235"
            vehicle.vehicleType == CAR_TYPE
        }
    }
    //endregion
}
