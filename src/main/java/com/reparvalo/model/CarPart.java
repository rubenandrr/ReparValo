package com.reparvalo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a replacement car part in the database.
 * 
 * This class maps to the "car_parts" table in the H2 database. It stores
 * key information about standard car body parts, including their name (which 
 * matches the IDs in the frontend SVG schematic), their estimated retail prices 
 * (both brand new and second-hand/used in CHF), and the standard labor time 
 * in hours required to replace or repair the part.
 * 
 * Concurrency access is safe as these records are read-only configuration data
 * loaded from data.sql, and are not mutated during standard calculation requests.
 */
@Entity
@Table(name = "car_parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarPart {

    /**
     * Unique identifier auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier name matching the SVG elements (e.g., "rear_right_door", "front_bumper").
     */
    private String name;

    /**
     * Estimated price of a brand new replacement part in Swiss Francs (CHF).
     */
    @Column(name = "new_price_chf")
    private Double newPriceChf;

    /**
     * Estimated price of a second-hand/recycled replacement part in Swiss Francs (CHF).
     */
    @Column(name = "used_price_chf")
    private Double usedPriceChf;

    /**
     * Standard labor duration in hours required by a mechanic to install and paint the part.
     */
    @Column(name = "repair_hours")
    private Double repairHours;
}