package com.reparvalo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Data Transfer Object representing the breakdown of a trade-in valuation.
 * 
 * This class holds the intermediate steps and the final buyback offer calculated
 * by the valuation engine. All currency values are in Swiss Francs (CHF).
 */
@Getter
@Builder
@ToString
public class TradeInEstimation {
    private String make;
    private String model;
    private Integer year;
    private Double mileage;
    private String condition;
    
    private Double baseValueChf;       // Brand new price of the model in Switzerland
    private Double depreciatedValue;   // Market value based strictly on vehicle age
    private Double mileageAdjustment;  // Positive bonus or negative penalty in CHF
    private Double conditionPenalty;   // Negative penalty in CHF based on wear/tear
    private Double dealerMargin;       // Dealer fee/margin subtracted in CHF
    private Double finalOffer;         // Final Swiss Franc buyback offer to the customer (minimum scrap value applied)
}