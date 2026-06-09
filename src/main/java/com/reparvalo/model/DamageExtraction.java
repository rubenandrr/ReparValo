package com.reparvalo.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Model representing the structured output of the damage analysis AI model.
 * 
 * Used by {@link com.reparvalo.service.AiService} to bind the LLM response
 * into a strongly-typed Java object.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DamageExtraction {
    
    /**
     * List of identified damaged part names matching our database identifiers
     * (e.g., "rear_right_door", "front_bumper").
     */
    private List<String> damagedParts;
    
    /**
     * Brief explanation or reasoning behind the AI's selection.
     */
    private String explanation;
}