package com.reparvalo.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload containing the trade-in estimation details and language preference
 * for generating the AI summary.
 */
@Getter
@Setter
public class ReportRequest {
    private TradeInEstimation estimation;
    private Boolean preferFrench;
}