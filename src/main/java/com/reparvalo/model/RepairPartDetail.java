package com.reparvalo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Details of the repair cost for a single car component.
 * 
 * Stores the chosen part price (new vs. used) and the calculated labor cost 
 * based on standard hours and the Geneva hourly rate.
 */
@Getter
@Builder
@ToString
public class RepairPartDetail {
    private String partName;
    private Double partPriceChf;       // Selected price (new or used)
    private Double laborHours;         // Duration of repair
    private Double laborCostChf;        // laborHours * 150 CHF/h
    private Double totalPartCostChf;    // partPriceChf + laborCostChf
}