package com.reparvalo.controller;

import com.reparvalo.model.Vehicle;
import com.reparvalo.repository.VehicleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing read-only endpoints for Swiss vehicle catalog metadata.
 * 
 * Provides HTTP endpoints enabling the frontend to retrieve the list of available 
 * vehicle templates (makes, models, and depreciation parameters) seeded in the database.
 * 
 * Concurrency is naturally safe as this controller performs read-only database queries.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Retrieves all available vehicle templates registered in the database catalog.
     * 
     * Accessible via HTTP GET request: GET /api/vehicles
     * 
     * @return a list of all {@link Vehicle} templates available for estimation
     */
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
}