package com.mettyoung.domain

import spock.lang.Specification

class ParkingSpaceSpec extends Specification {

    def "should be able to allocate parking lots given car and motorcycle lots"() {
        given:
        def carType = new VehicleType("Car", 2 as BigDecimal)
        def motorcycleType = new VehicleType("Motorcycle", 1 as BigDecimal)
        def carLots = new VehicleLots(carType, 10)
        def motorcycleLots = new VehicleLots(motorcycleType, 5)

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
}
