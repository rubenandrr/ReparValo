package com.reparvalo.service;

import com.reparvalo.model.DamageExtraction;
import com.reparvalo.model.TradeInEstimation;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service orchestrating AI interactions with Google Gemini via Spring AI.
 * 
 * Provides features to parse free-text descriptions of vehicle damage into
 * structured data, and to generate persuasive, localized Swiss trade-in pitches.
 * 
 * Concurrency is thread-safe as this service remains stateless.
 */
@Service
public class AiService {

    @Autowired
    private ChatModel chatModel;

    /**
     * Analyzes free-form text detailing car damage and extracts matching database part names.
     * 
     * @param userText natural language description of vehicle damage (e.g., "front bumper is scratched")
     * @return a structured {@link DamageExtraction} containing matching parts and an explanation
     */
    public DamageExtraction extractDamages(String userText) {
        // Initialize the output parser for DamageExtraction class
        BeanOutputConverter<DamageExtraction> parser = new BeanOutputConverter<>(DamageExtraction.class);
        String formatInstructions = parser.getFormat();

        // Create prompt template requesting structured JSON conforming to the schema
        String promptText = """
                You are an expert automotive damage inspector. Your task is to analyze the following user description
                of physical car damage:
                
                Description: "{userText}"
                
                You must identify all damaged parts from this exact list of valid components:
                [rear_right_door, rear_left_door, front_right_door, front_left_door, front_bumper, rear_bumper, left_headlight, right_headlight, left_mirror, right_mirror, hood, trunk]
                
                Strictly match the parts. If the user mentions "back door" or "posterior door" on the right side, match "rear_right_door".
                If no matching parts are found, return an empty list.
                
                {formatInstructions}
                """;

        PromptTemplate template = new PromptTemplate(promptText);
        template.add("userText", userText);
        template.add("formatInstructions", formatInstructions);

        Prompt prompt = template.create();
        ChatResponse response = chatModel.call(prompt);
        String responseContent = response.getResult().getOutput().getText();

        // Parse and return the structured object
        return parser.convert(responseContent);
    }

    /**
     * Generates a professional sales pitch/report for a Swiss car dealer to present to a customer.
     * 
     * @param estimation the calculated trade-in estimation details
     * @param preferFrench true to write the pitch in French, false for English
     * @return a markdown-formatted Swiss trade-in justification letter in the chosen language
     */
    public String generateTradeInReport(TradeInEstimation estimation, boolean preferFrench) {
        String promptText = """
                You are a professional Swiss car dealer located in Geneva. Prepare a professional, respectful, 
                and persuasive buyback pitch (written in {language}) for a customer based on the following estimation:
                
                - Vehicle: {make} {model} ({year})
                - Mileage: {mileage} km
                - Visual/Aesthetic Condition: {condition}
                - Original Swiss Price (new): {baseValueChf} CHF
                - Estimated Depreciation: -{depreciationAmount} CHF
                - Mileage Adjustment (bonus/penalty): {mileageAdjustment} CHF
                - Condition Wear Penalty: {conditionPenalty} CHF
                - Dealer Reconditioning & Margin: -{dealerMargin} CHF
                - Final Buyback Offer: {finalOffer} CHF
                
                Instructions:
                - Write the pitch in {language}, addressing the customer politely.
                - Explain logically how the Swiss market factors (age, mileage, local Geneva reconditioning costs) led to this offer.
                - Keep it clear, concise, and limit the response to around 150-200 words.
                - Format the response with clean markdown paragraphs. Do not add salutations like "Chers clients" or "Dear customer", start directly.
                """;

        double depreciationAmount = estimation.getBaseValueChf() - estimation.getDepreciatedValue();
        String targetLanguage = preferFrench ? "French" : "English";

        PromptTemplate template = new PromptTemplate(promptText);
        template.add("language", targetLanguage);
        template.add("make", estimation.getMake());
        template.add("model", estimation.getModel());
        template.add("year", estimation.getYear());
        template.add("mileage", estimation.getMileage());
        template.add("condition", estimation.getCondition());
        template.add("baseValueChf", estimation.getBaseValueChf());
        template.add("depreciationAmount", depreciationAmount);
        template.add("mileageAdjustment", estimation.getMileageAdjustment());
        template.add("conditionPenalty", estimation.getConditionPenalty());
        template.add("dealerMargin", estimation.getDealerMargin());
        template.add("finalOffer", estimation.getFinalOffer());

        Prompt prompt = template.create();
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}