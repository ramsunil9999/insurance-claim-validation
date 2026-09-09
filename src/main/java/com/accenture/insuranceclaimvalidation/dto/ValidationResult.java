package com.accenture.insuranceclaimvalidation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    private boolean valid;

    private List<String> errors;

    private Integer riskScore;

    private String riskLevel;

    private Double medicalNecessityScore;

    private String coverageStatus;

    private Boolean waitingPeriodSatisfied;

    private Boolean duplicateDetected;

    private Boolean procedureDiagnosisValid;

    private Boolean doctorSpecialtyValid;

    private Boolean hospitalCapabilityValid;

    private Boolean priorAuthorizationMatched;

    private List<String> missingDocuments;

    private List<String> riskFactors;

}