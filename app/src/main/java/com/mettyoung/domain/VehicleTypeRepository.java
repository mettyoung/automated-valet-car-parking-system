package com.mettyoung.domain;

import java.util.Optional;

public interface VehicleTypeRepository {
    Optional<VehicleType> findByName(String name);
}
