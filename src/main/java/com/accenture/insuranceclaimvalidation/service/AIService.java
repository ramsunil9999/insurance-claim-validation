package com.accenture.insuranceclaimvalidation.service;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;

public interface AIService {

    ClaimDetails extractClaimDetails(String extractedText);

}