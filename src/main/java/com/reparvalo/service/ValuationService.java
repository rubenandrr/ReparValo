package com.reparvalo.service;

import com.reparvalo.model.CarPart;
import com.reparvalo.model.RepairEstimation;
import com.reparvalo.model.RepairPartDetail;
import com.reparvalo.model.TradeInEstimation;
import com.reparvalo.model.Vehicle;
import com.reparvalo.repository.CarPartRepository;
import com.reparvalo.repository.VehicleRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service handling business logic for trade-in and repair valuations.
 * 
 * Computes vehicle trade-in prices based on Swiss depreciation models,
 * mileage adjustments, and physical wear. Also computes itemized repair
 * invoices using local Geneva labor rates (150 CHF/h).
 * 
 * Concurrency is naturally thread-safe as this service is stateless.
 */
@Service
public class ValuationService {

    public static final double GENEVA_HOURLY_LABOR_RATE = 150.0;
    public static final double STANDARD_ANNUAL_MILEAGE = 15000.0;
    public static final double DEALER_MARGIN_PERCENTAGE = 0.15;
    public static final double MINIMUM_SCRAP_VALUE_CHF = 500.0;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CarPartRepository carPartRepository;

    /**
     * Estimates the trade-in value of a vehicle based on its technical specifications.
     * 
     * @param make the manufacturer name of the vehicle
     * @param model the model name of the vehicle
     * @param year the year of first registration
     * @param mileage the current odometer reading in kilometers
     * @param condition the aesthetic condition (excellent, good, medium, poor)
     * @return a detailed {@link TradeInEstimation} report
     * @throws IllegalArgumentException if the make and model cannot be found in the catalog
     */
    public TradeInEstimation calculateTradeInValue(String make, String model, int year, double mileage, String condition) {
        // 1. Fetch template from database
        Vehicle vehicle = vehicleRepository.findByMakeIgnoreCaseAndModelIgnoreCase(make, model)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle template not found for make: " + make + ", model: " + model));

        double baseValue = vehicle.getBaseValueChf();
        double rate = vehicle.getDepreciationRate();

        // 2. Calculate age depreciation
        int currentYear = LocalDate.now().getYear();
        int age = Math.max(0, currentYear - year);
        double depreciatedValue = baseValue * Math.pow(1 - rate, age);

        // 3. Mileage adjustment (Standard: 15,000 km/year)
        double expectedMileage = age * STANDARD_ANNUAL_MILEAGE;
        double mileageDiff = expectedMileage - mileage; // positive = under-driven (bonus), negative = over-driven (penalty)
        double mileageAdjustment;

        if (mileageDiff > 0) {
            // Bonus for low mileage (0.05 CHF per km saved) capped at +15% of depreciated value
            double rawBonus = mileageDiff * 0.05;
            double maxBonus = depreciatedValue * 0.15;
            mileageAdjustment = Math.min(rawBonus, maxBonus);
        } else {
            // Penalty for high mileage (0.08 CHF per excess km) capped at -25% of depreciated value
            double rawPenalty = mileageDiff * 0.08; // negative value
            double maxPenalty = -depreciatedValue * 0.25;
            mileageAdjustment = Math.max(rawPenalty, maxPenalty);
        }

        // 4. Physical condition penalty (applied to depreciated value + mileage subtotal)
        double conditionFactor;
        switch (condition.toLowerCase()) {
            case "excellent":
                conditionFactor = 0.00;
                break;
            case "good":
                conditionFactor = 0.05;
                break;
            case "poor":
                conditionFactor = 0.30;
                break;
            case "medium":
            default:
                conditionFactor = 0.15;
                break;
        }
        double conditionPenalty = - (depreciatedValue + mileageAdjustment) * conditionFactor;

        // 5. Compute totals & dealer margin
        double valuationSubtotal = depreciatedValue + mileageAdjustment + conditionPenalty;
        double dealerMargin = valuationSubtotal * DEALER_MARGIN_PERCENTAGE;
        double finalOffer = Math.max(MINIMUM_SCRAP_VALUE_CHF, valuationSubtotal - dealerMargin);

        return TradeInEstimation.builder()
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(year)
                .mileage(mileage)
                .condition(condition)
                .baseValueChf(baseValue)
                .depreciatedValue(depreciatedValue)
                .mileageAdjustment(mileageAdjustment)
                .conditionPenalty(conditionPenalty)
                .dealerMargin(dealerMargin)
                .finalOffer(finalOffer)
                .build();
    }

    /**
     * Computes the total repair estimate for a list of damaged parts.
     * 
     * @param partNames a list of part identifier names (matching SVG layout)
     * @param useUsedParts true if the user wants used/second-hand parts pricing
     * @return a detailed {@link RepairEstimation} invoice
     */
    public RepairEstimation calculateRepairCost(List<String> partNames, boolean useUsedParts) {
        List<RepairPartDetail> details = new ArrayList<>();
        double totalPartsCost = 0.0;
        double totalLaborCost = 0.0;

        for (String partName : partNames) {
            CarPart part = carPartRepository.findByName(partName).orElse(null);
            if (part != null) {
                // Determine part price based on selection
                double partPrice = useUsedParts ? part.getUsedPriceChf() : part.getNewPriceChf();
                double laborCost = part.getRepairHours() * GENEVA_HOURLY_LABOR_RATE;
                double totalPartCost = partPrice + laborCost;

                totalPartsCost += partPrice;
                totalLaborCost += laborCost;

                details.add(RepairPartDetail.builder()
                        .partName(part.getName())
                        .partPriceChf(partPrice)
                        .laborHours(part.getRepairHours())
                        .laborCostChf(laborCost)
                        .totalPartCostChf(totalPartCost)
                        .build());
            }
        }

        return RepairEstimation.builder()
                .details(details)
                .totalPartsCostChf(totalPartsCost)
                .totalLaborCostChf(totalLaborCost)
                .totalRepairCostChf(totalPartsCost + totalLaborCost)
                .build();
    }
}