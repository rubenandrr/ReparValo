package com.reparvalo.service;

import com.reparvalo.model.DamageExtraction;
import com.reparvalo.model.TradeInEstimation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AiService} verifying model mapping and prompt generation.
 * 
 * Simulates interactions with the Gemini LLM by mocking the {@link ChatClient}
 * responses, ensuring JSON output parsing and report templates work correctly.
 */
@ExtendWith(MockitoExtension.class)
public class AiServiceTest {

    @Mock
    private ChatModel chatModel;

    @InjectMocks
    private AiService aiService;

    /**
     * Test case: Verifies that a valid JSON response from Gemini is correctly
     * parsed into a structured {@link DamageExtraction} object.
     */
    @Test
    public void testExtractDamagesSuccess() {
        // Given
        String mockJsonResponse = """
                {
                  "damagedParts": ["rear_right_door", "right_mirror"],
                  "explanation": "The user reported scraping a wall on the right side."
                }
                """;
        
        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        AssistantMessage mockAssistantMessage = mock(AssistantMessage.class);

        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(mockAssistantMessage);
        when(mockAssistantMessage.getText()).thenReturn(mockJsonResponse);

        // When
        DamageExtraction result = aiService.extractDamages("I scraped the right side of my car against a wall.");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDamagedParts()).containsExactly("rear_right_door", "right_mirror");
        assertThat(result.getExplanation()).contains("scraping a wall");
    }

    /**
     * Test case: Verifies that the sales pitch report is successfully generated
     * by rendering the template and calling the AI client.
     */
    @Test
    public void testGenerateTradeInReport() {
        // Given
        TradeInEstimation mockEstimation = TradeInEstimation.builder()
                .make("Volkswagen")
                .model("Golf")
                .year(2020)
                .mileage(80000.0)
                .condition("good")
                .baseValueChf(32000.0)
                .depreciatedValue(20000.0)
                .mileageAdjustment(-500.0)
                .conditionPenalty(-1000.0)
                .dealerMargin(2775.0)
                .finalOffer(15725.0)
                .build();

        String mockReport = "Cher client, suite à l'analyse de votre Golf de 2020...";

        ChatResponse mockResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        AssistantMessage mockAssistantMessage = mock(AssistantMessage.class);

        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(mockAssistantMessage);
        when(mockAssistantMessage.getText()).thenReturn(mockReport);

        // When - Requesting report in French
        String report = aiService.generateTradeInReport(mockEstimation, true);

        // Then
        assertThat(report).isEqualTo(mockReport);
    }
}