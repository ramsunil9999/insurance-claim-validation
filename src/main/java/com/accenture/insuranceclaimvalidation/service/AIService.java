package com.accenture.insuranceclaimvalidation.service;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationDetails;

public interface AIService {

    ClaimDetails extractClaimDetails(String extractedText);

    PriorAuthorizationDetails extractPriorAuthorizationDetails(String extractedText);

}