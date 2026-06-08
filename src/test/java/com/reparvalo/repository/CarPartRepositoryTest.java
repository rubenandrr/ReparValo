package com.reparvalo.repository;

import com.reparvalo.model.CarPart;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CarPartRepository} using an in-memory database.
 * 
 * This class ensures that JPA mappings and custom query methods for {@link CarPart}
 * are functioning correctly with the embedded H2 database under transactional rollback rules.
 */
@DataJpaTest
public class CarPartRepositoryTest {

    @Autowired
    private CarPartRepository carPartRepository;

    /**
     * Verifies that saving a car part persists it and that searching for it by name
     * retrieves the correct configuration parameters.
     */
    @Test
    public void testSaveAndFindCarPartByName() {
        // Given
        CarPart part = CarPart.builder()
                .name("rear_right_door")
                .newPriceChf(550.0)
                .usedPriceChf(180.0)
                .repairHours(3.5)
                .build();
        carPartRepository.save(part);

        // When
        Optional<CarPart> foundPart = carPartRepository.findByName("rear_right_door");

        // Then
        assertThat(foundPart).isPresent();
        assertThat(foundPart.get().getName()).isEqualTo("rear_right_door");
        assertThat(foundPart.get().getNewPriceChf()).isEqualTo(550.0);
        assertThat(foundPart.get().getUsedPriceChf()).isEqualTo(180.0);
        assertThat(foundPart.get().getRepairHours()).isEqualTo(3.5);
    }

    /**
     * Verifies that searching for an unregistered part name returns an empty optional.
     */
    @Test
    public void testFindCarPartNotFound() {
        // When
        Optional<CarPart> foundPart = carPartRepository.findByName("engine_block");

        // Then
        assertThat(foundPart).isEmpty();
    }
}