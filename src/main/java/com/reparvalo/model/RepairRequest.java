package com.reparvalo.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Input request payload for vehicle repair estimation.
 * 
 * Captures selected SVG part names and spare part conditions.
 */
@Getter
@Setter
@ToString
public class RepairRequest {
    private List<String> partNames;
    private Boolean useUsedParts;
}