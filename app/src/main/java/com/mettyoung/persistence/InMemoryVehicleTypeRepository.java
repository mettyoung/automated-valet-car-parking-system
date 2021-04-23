package com.mettyoung.persistence;

import com.mettyoung.domain.VehicleType;
import com.mettyoung.domain.VehicleTypeRepository;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class InMemoryVehicleTypeRepository implements VehicleTypeRepository {

    private final Map<String, VehicleType> VEHICLE_TYPES = Map.of(
            "CAR", new VehicleType("Car", new BigDecimal(2)),
            "MOTORCYCLE", new VehicleType("Motorcycle", new BigDecimal(1))
    );

    @Override
    public Optional<VehicleType> findByName(String name) {
        return Optional.ofNullable(VEHICLE_TYPES.get(StringUtils.upperCase(name)));
    }
}
