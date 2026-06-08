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
 * Entity representing a vehicle model in the Swiss database.
 * 
 * This class maps to the "vehicles" table in the H2 database. It stores the
 * manufacturer and model name, along with the retail base price in Swiss Francs (CHF)
 * and its specific annual depreciation rate. This rate is used by the valuation 
 * engine to calculate the vehicle's market value over time.
 * 
 * Concurrency safety is maintained by Hibernate's default transaction handling,
 * as database entries are read-only for customers and only modified by administrators.
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    /**
     * Unique identifier auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Manufacturer name (e.g., "Tesla", "Volkswagen").
     */
    private String make;

    /**
     * Model name (e.g., "Model 3", "Golf").
     */
    private String model;

    /**
     * Base value of the vehicle brand new in Switzerland (expressed in CHF).
     */
    @Column(name = "base_value_chf")
    private Double baseValueChf;

    /**
     * Annual depreciation rate (e.g., 0.15 represents a 15% reduction in value per year).
     */
    @Column(name = "depreciation_rate")
    private Double depreciationRate;
}