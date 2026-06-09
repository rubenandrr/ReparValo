package com.reparvalo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Input request payload for trade-in buyback calculations.
 * 
 * Captures user input from the dealer trade-in form.
 */
@Getter
@Setter
@ToString
public class TradeInRequest {
    private String make;
    private String model;
    private Integer year;
    private Double mileage;
    private String condition;
}