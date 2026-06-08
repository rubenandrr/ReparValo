package com.reparvalo.repository;

import com.reparvalo.model.Vehicle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Vehicle} persistence operations.
 * 
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * against the H2 database. It includes a custom query method to look up a vehicle 
 * by its make and model ignoring case sensitivity, which is vital for the valuation engine.
 * 
 * Concurrency is handled by Spring Data JPA's underlying transaction manager.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Finds a vehicle in the database matching the specified make and model, ignoring case.
     * 
     * @param make the manufacturer name (e.g., "Volkswagen")
     * @param model the model name (e.g., "Golf")
     * @return an {@link Optional} containing the matched {@link Vehicle}, or empty if not found
     */
    Optional<Vehicle> findByMakeAndModelIgnoreCase(String make, String model);
}