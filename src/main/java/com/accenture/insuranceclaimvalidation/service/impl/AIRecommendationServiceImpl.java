package com.accenture.insuranceclaimvalidation.service.impl;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.accenture.insuranceclaimvalidation.dto.ClaimAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;
import com.accenture.insuranceclaimvalidation.enums.Recommendation;
import com.accenture.insuranceclaimvalidation.service.AIRecommendationService;
import com.accenture.insuranceclaimvalidation.util.PromptTemplates;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIRecommendationServiceImpl implements AIRecommendationService {

        private final ChatClient chatClient;

        private final ObjectMapper objectMapper;

        @Override
        public RecommendationResult recommendClaim(ClaimAssessmentContext context) {

                long startTime = System.currentTimeMillis();
                try {
                        String prompt = PromptTemplates.buildRecommendationPrompt(context);

                        log.info("Generating AI recommendation for Policy Number: {}", context.getClaimDetails().getPolicyNumber());

                        String aiResponse = chatClient.prompt()
                                        .user(prompt)
                                        .call()
                                        .content();

                        log.debug("Raw AI Recommendation Response : {}", aiResponse);

                        String cleanedResponse = cleanJson(aiResponse);

                        RecommendationResult result = objectMapper.readValue(cleanedResponse,RecommendationResult.class);

                        validateRecommendation(result);

                        long endTime = System.currentTimeMillis();

                        log.info("Recommendation Generated Successfully | Policy={} | Recommendation={} | Confidence={} | Time={} ms",
                                        context.getClaimDetails().getPolicyNumber(), result.getRecommendation(), result.getConfidence(), (endTime - startTime));

                        return result;

                } catch (Exception ex) {
                        log.error("Failed to generate AI recommendation for Policy Number: {}", context.getClaimDetails().getPolicyNumber(), ex);

                        return buildFallbackRecommendation();

                }

        }

        /**
         * Removes markdown formatting if the LLM returns
         * ```json ... ```
         */
        private String cleanJson(String response) {
                if (response == null) {
                        return "";
                }
                String cleaned = response.trim();
                if (cleaned.startsWith("```")) {
                        cleaned = cleaned.replace("```json", "")
                                        .replace("```", "").trim();
                }

                int firstBrace = cleaned.indexOf('{');
                int lastBrace = cleaned.lastIndexOf('}');

                if (firstBrace >= 0 && lastBrace > firstBrace) {
                        cleaned = cleaned.substring(firstBrace, lastBrace + 1);
                }

                return cleaned;

        }

        /**
         * Ensures recommendation response is usable.
         */
        private void validateRecommendation(RecommendationResult result) {
                if (result == null) { throw new IllegalStateException("Recommendation response is null."); }

                if (result.getRecommendation() == null) {
                        result.setRecommendation(Recommendation.MANUAL_REVIEW);
                }

                if (result.getReason() == null || result.getReason().isBlank()) {
                        result.setReason("No recommendation reason provided by AI.");
                }

                if (result.getObservations() == null) {
                        result.setObservations(List.of());
                }

                if (result.getConfidence() == null) {
                        result.setConfidence(0.50);
                } else {
                        if (result.getConfidence() < 0) {
                                result.setConfidence(0.0);
                        }
                        if (result.getConfidence() > 1.0) {
                                result.setConfidence(1.0);
                        }
                }

        }

        private RecommendationResult buildFallbackRecommendation() {
                return RecommendationResult.builder()
                                .recommendation(Recommendation.MANUAL_REVIEW)
                                .reason("AI recommendation could not be generated. Manual review required.")
                                .confidence(0.0)
                                .observations(List.of("AI recommendation service unavailable.", "Claim requires manual assessment."))
                                .build();
        }

}