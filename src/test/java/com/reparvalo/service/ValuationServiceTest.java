package com.reparvalo.service;

import com.reparvalo.model.CarPart;
import com.reparvalo.model.RepairEstimation;
import com.reparvalo.model.TradeInEstimation;
import com.reparvalo.model.Vehicle;
import com.reparvalo.repository.CarPartRepository;
import com.reparvalo.repository.VehicleRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ValuationService} using Mockito for repository isolation.
 * 
 * This class validates all calculation logic, checking age depreciation,
 * mileage adjustments (bonus/penalty ceilings), condition multipliers,
 * dealer margins, and scrap value floors in CHF.
 */
@ExtendWith(MockitoExtension.class)
public class ValuationServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CarPartRepository carPartRepository;

    @InjectMocks
    private ValuationService valuationService;

    /**
     * Test case: Verifies standard trade-in calculation for a 5-year-old vehicle
     * with average mileage and 'good' condition.
     */
    @Test
    public void testCalculateTradeInValueStandard() {
        // Given
        int currentYear = LocalDate.now().getYear();
        int vehicleYear = currentYear - 5; // exactly 5 years old
        double mileage = 75000.0; // 5 years * 15,000 = exactly standard mileage (0 adjustment)
        
        Vehicle mockVehicle = Vehicle.builder()
                .make("Volkswagen")
                .model("Golf")
                .baseValueChf(32000.0)
                .depreciationRate(0.12)
                .build();
        
        when(vehicleRepository.findByMakeIgnoreCaseAndModelIgnoreCase("Volkswagen", "Golf"))
                .thenReturn(Optional.of(mockVehicle));

        // When
        TradeInEstimation estimation = valuationService.calculateTradeInValue(
                "Volkswagen", "Golf", vehicleYear, mileage, "good");

        // Then
        // Depreciated value = 32000 * (1 - 0.12)^5 = 16887.65 CHF
        double expectedDepreciated = 32000.0 * Math.pow(0.88, 5);
        assertThat(estimation.getDepreciatedValue()).isEqualTo(expectedDepreciated);
        assertThat(estimation.getMileageAdjustment()).isEqualTo(0.0);
        
        // Good condition penalty is -5% of depreciated value
        double expectedConditionPenalty = -expectedDepreciated * 0.05;
        assertThat(estimation.getConditionPenalty()).isEqualTo(expectedConditionPenalty);
        
        // Subtotal = expectedDepreciated + expectedConditionPenalty
        double expectedSubtotal = expectedDepreciated + expectedConditionPenalty;
        double expectedMargin = expectedSubtotal * 0.15;
        assertThat(estimation.getDealerMargin()).isEqualTo(expectedMargin);
        
        double expectedOffer = expectedSubtotal - expectedMargin;
        assertThat(estimation.getFinalOffer()).isEqualTo(expectedOffer);
    }

    /**
     * Test case: Verifies that the minimum scrap floor (500 CHF) is applied
     * when the vehicle depreciates to an extremely low value.
     */
    @Test
    public void testCalculateTradeInValueScrapFloor() {
        // Given
        int currentYear = LocalDate.now().getYear();
        int vehicleYear = currentYear - 25; // 25 years old, heavily depreciated
        
        Vehicle mockVehicle = Vehicle.builder()
                .make("Renault")
                .model("Clio")
                .baseValueChf(20000.0)
                .depreciationRate(0.15)
                .build();
        
        when(vehicleRepository.findByMakeIgnoreCaseAndModelIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(mockVehicle));

        // When
        TradeInEstimation estimation = valuationService.calculateTradeInValue(
                "Renault", "Clio", vehicleYear, 300000.0, "poor");

        // Then
        assertThat(estimation.getFinalOffer()).isEqualTo(500.0); // Minimum scrap value
    }

    /**
     * Test case: Verifies repair cost estimation using brand new parts pricing
     * and local Geneva labor rates.
     */
    @Test
    public void testCalculateRepairCostNewParts() {
        // Given
        CarPart door = CarPart.builder()
                .name("rear_right_door")
                .newPriceChf(550.0)
                .usedPriceChf(180.0)
                .repairHours(3.5)
                .build();
        
        CarPart bumper = CarPart.builder()
                .name("rear_bumper")
                .newPriceChf(480.0)
                .usedPriceChf(160.0)
                .repairHours(2.5)
                .build();

        when(carPartRepository.findByName("rear_right_door")).thenReturn(Optional.of(door));
        when(carPartRepository.findByName("rear_bumper")).thenReturn(Optional.of(bumper));

        // When - Estimating new parts repair
        RepairEstimation estimation = valuationService.calculateRepairCost(
                Arrays.asList("rear_right_door", "rear_bumper"), false);

        // Then
        // Parts total = 550 + 480 = 1030 CHF
        assertThat(estimation.getTotalPartsCostChf()).isEqualTo(1030.0);
        // Labor hours = 3.5 + 2.5 = 6.0 hours. Labor cost = 6.0 * 150 = 900 CHF
        assertThat(estimation.getTotalLaborCostChf()).isEqualTo(900.0);
        // Total = 1030 + 900 = 1930 CHF
        assertThat(estimation.getTotalRepairCostChf()).isEqualTo(1930.0);
        assertThat(estimation.getDetails()).hasSize(2);
    }

    /**
     * Test case: Verifies repair cost estimation using second-hand parts pricing
     * and local Geneva labor rates.
     */
    @Test
    public void testCalculateRepairCostUsedParts() {
        // Given
        CarPart door = CarPart.builder()
                .name("rear_right_door")
                .newPriceChf(550.0)
                .usedPriceChf(180.0)
                .repairHours(3.5)
                .build();

        when(carPartRepository.findByName("rear_right_door")).thenReturn(Optional.of(door));

        // When - Estimating used parts repair
        RepairEstimation estimation = valuationService.calculateRepairCost(
                Arrays.asList("rear_right_door"), true);

        // Then
        // Used part price = 180 CHF. Labor hours = 3.5. Labor cost = 3.5 * 150 = 525 CHF.
        // Total = 180 + 525 = 705 CHF
        assertThat(estimation.getTotalPartsCostChf()).isEqualTo(180.0);
        assertThat(estimation.getTotalLaborCostChf()).isEqualTo(525.0);
        assertThat(estimation.getTotalRepairCostChf()).isEqualTo(705.0);
    }
}