package com.reparvalo.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Data Transfer Object representing a full breakdown of the repair estimate.
 * 
 * Aggregates individual part repairs and computes the total parts, labor,
 * and overall project costs in Swiss Francs (CHF).
 */
@Getter
@Builder
@ToString
public class RepairEstimation {
    private List<RepairPartDetail> details;
    private Double totalPartsCostChf;
    private Double totalLaborCostChf;
    private Double totalRepairCostChf;
}