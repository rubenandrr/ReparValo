package com.reparvalo.controller;

import com.reparvalo.model.RepairEstimation;
import com.reparvalo.model.RepairRequest;
import com.reparvalo.model.TradeInEstimation;
import com.reparvalo.model.TradeInRequest;
import com.reparvalo.service.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing endpoints for trade-in and repair calculations.
 * 
 * Handles incoming POST requests with user parameters, processes them via the 
 * {@link ValuationService}, and returns calculated estimates in CHF.
 * 
 * Concurrency is thread-safe as this controller is stateless and delegates to stateless services.
 */
@RestController
@RequestMapping("/api/estimate")
public class EstimationController {

    @Autowired
    private ValuationService valuationService;

    /**
     * Calculates the trade-in buyback value for a vehicle.
     * 
     * Accessible via HTTP POST request: POST /api/estimate/trade-in
     * 
     * @param request the trade-in parameters (make, model, mileage, etc.)
     * @return a {@link ResponseEntity} containing the trade-in valuation report
     */
    @PostMapping("/trade-in")
    public ResponseEntity<TradeInEstimation> getTradeInEstimate(@RequestBody TradeInRequest request) {
        try {
            TradeInEstimation estimation = valuationService.calculateTradeInValue(
                    request.getMake(),
                    request.getModel(),
                    request.getYear(),
                    request.getMileage(),
                    request.getCondition()
            );
            return ResponseEntity.ok(estimation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Calculates the repair quote for a list of damaged parts.
     * 
     * Accessible via HTTP POST request: POST /api/estimate/repair
     * 
     * @param request the list of damaged parts and spare part preference
     * @return a {@link ResponseEntity} containing the detailed repair invoice
     */
    @PostMapping("/repair")
    public ResponseEntity<RepairEstimation> getRepairEstimate(@RequestBody RepairRequest request) {
        RepairEstimation estimation = valuationService.calculateRepairCost(
                request.getPartNames(),
                request.getUseUsedParts() != null && request.getUseUsedParts()
        );
        return ResponseEntity.ok(estimation);
    }
}