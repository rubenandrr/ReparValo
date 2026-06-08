package com.reparvalo.repository;

import com.reparvalo.model.CarPart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link CarPart} persistence operations.
 * 
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * against the H2 database. It includes a custom query method to retrieve a specific
 * car part configuration by its unique identifier name (e.g., "rear_right_door").
 * 
 * Concurrency is handled by Spring Data JPA's underlying transaction manager.
 */
@Repository
public interface CarPartRepository extends JpaRepository<CarPart, Long> {

    /**
     * Finds a car part in the database by its unique string identifier name.
     * 
     * @param name the unique name matching the SVG element (e.g., "rear_right_door")
     * @return an {@link Optional} containing the matched {@link CarPart}, or empty if not found
     */
    Optional<CarPart> findByName(String name);
}