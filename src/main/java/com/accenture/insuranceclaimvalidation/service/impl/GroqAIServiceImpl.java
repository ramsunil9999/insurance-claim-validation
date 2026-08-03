package com.accenture.insuranceclaimvalidation.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.exception.AIException;
import com.accenture.insuranceclaimvalidation.service.AIService;
import com.accenture.insuranceclaimvalidation.util.PromptTemplates;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroqAIServiceImpl implements AIService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public ClaimDetails extractClaimDetails(String extractedText) {

        try {

            log.info("Generating extraction prompt.");

            String prompt = PromptTemplates.buildClaimExtractionPrompt(extractedText);

            String aiResponse = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.debug("Raw AI Extraction Response:\n{}", aiResponse);

            aiResponse = cleanJson(aiResponse);

            ClaimDetails claimDetails = objectMapper.readValue(aiResponse, ClaimDetails.class);

            calculateLengthOfStay(claimDetails);

            log.info("Claim extraction completed successfully for Policy={}", claimDetails.getPolicyNumber());

            return claimDetails;

        } catch (Exception ex) {

            log.error("Failed to extract claim details.", ex);

            throw new AIException("Failed to process AI response.", ex);

        }

    }

    private String cleanJson(String response) {

        if (response == null) {
            return "";
        }

        response = response
                .replace("```json", "")
                .replace("```", "")
                .trim();

        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');

        if (start >= 0 && end >= start) {
            response = response.substring(start, end + 1);
        }

        return response;

    }

    private void calculateLengthOfStay(
            ClaimDetails claimDetails) {

        if (claimDetails.getAdmissionDate() == null || claimDetails.getDischargeDate() == null) {
            return;
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(claimDetails.getAdmissionDate(), claimDetails.getDischargeDate());

        if (days >= 0) {
            claimDetails.setLengthOfStay((int) days);
        }

    }

}