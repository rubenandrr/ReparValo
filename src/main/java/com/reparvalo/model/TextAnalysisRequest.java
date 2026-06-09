package com.reparvalo.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Request payload containing the free-form text description of vehicle damages.
 */
@Getter
@Setter
public class TextAnalysisRequest {
    private String text;
}