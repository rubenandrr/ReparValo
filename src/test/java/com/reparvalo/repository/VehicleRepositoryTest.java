package com.reparvalo.repository;

import com.reparvalo.model.Vehicle;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link VehicleRepository} using an embedded database.
 * 
 * This test class utilizes {@link DataJpaTest} to configure an in-memory database
 * (H2) and configure Spring Data JPA. All tests run within a transaction which
 * is automatically rolled back at the end of each test to ensure isolation.
 */
@DataJpaTest
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Verifies that saving a vehicle correctly persists it in the database,
     * and that searching for it by make and model ignoring case works as expected.
     */
    @Test
    public void testSaveAndFindVehicleIgnoreCase() {
        // Given
        Vehicle vehicle = Vehicle.builder()
                .make("Tesla")
                .model("Model 3")
                .baseValueChf(46000.0)
                .depreciationRate(0.18)
                .build();
        vehicleRepository.save(vehicle);

        // When - Querying with different letter casing
        Optional<Vehicle> foundVehicle = vehicleRepository.findByMakeAndModelIgnoreCase("teSLa", "mODel 3");

        // Then
        assertThat(foundVehicle).isPresent();
        assertThat(foundVehicle.get().getMake()).isEqualTo("Tesla");
        assertThat(foundVehicle.get().getModel()).isEqualTo("Model 3");
        assertThat(foundVehicle.get().getBaseValueChf()).isEqualTo(46000.0);
        assertThat(foundVehicle.get().getDepreciationRate()).isEqualTo(0.18);
    }

    /**
     * Verifies that looking up a non-existent vehicle returns an empty optional.
     */
    @Test
    public void testFindVehicleNotFound() {
        // When
        Optional<Vehicle> foundVehicle = vehicleRepository.findByMakeAndModelIgnoreCase("Ferrari", "Enzo");

        // Then
        assertThat(foundVehicle).isEmpty();
    }
}