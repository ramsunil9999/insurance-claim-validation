package com.accenture.insuranceclaimvalidation.dto;

import java.util.List;

import com.accenture.insuranceclaimvalidation.enums.Recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResult {

    private Recommendation recommendation;

    private String reason;

    private Double confidence;

    private List<String> observations;

    private Integer riskScore;

    private String riskLevel;

    private Double medicalNecessityScore;

    private String coverageStatus;

    private Boolean waitingPeriodSatisfied;

    private Boolean duplicateDetected;

    private Boolean priorAuthorizationMatched;

    private Boolean procedureDiagnosisValid;

    private Boolean doctorSpecialtyValid;

    private Boolean hospitalCapabilityValid;

    private List<String> riskFactors;

}