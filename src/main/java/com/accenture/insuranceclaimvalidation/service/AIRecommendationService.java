package com.accenture.insuranceclaimvalidation.service;

import com.accenture.insuranceclaimvalidation.dto.ClaimAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;

public interface AIRecommendationService {

    RecommendationResult recommendClaim(ClaimAssessmentContext context);

}