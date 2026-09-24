package com.accenture.insuranceclaimvalidation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorAuthorizationAssessmentContext {

    private PriorAuthorizationDetails priorAuthorizationDetails;

    private ValidationResult validationResult;

    private String policyContext;
}