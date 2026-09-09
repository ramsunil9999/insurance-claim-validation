package com.accenture.insuranceclaimvalidation.mapper;

import org.springframework.stereotype.Component;

import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationDetails;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;
import com.accenture.insuranceclaimvalidation.entity.PriorAuthorization;
import com.accenture.insuranceclaimvalidation.enums.ClaimStatus;

@Component
public class PriorAuthorizationMapper {

    public PriorAuthorization toEntity(PriorAuthorizationDetails details) {

        return PriorAuthorization.builder()

                // ===========================
                // Patient Information
                // ===========================

                .patientName(details.getPatientName())
                .age(details.getAge())
                .gender(details.getGender())

                // ===========================
                // Insurance Information
                // ===========================

                .policyNumber(details.getPolicyNumber())
                .memberId(details.getMemberId())
                .insurancePlan(details.getInsurancePlan())
                .policyStartDate(details.getPolicyStartDate())
                .waitingPeriodStatus(details.getWaitingPeriodStatus())

                // ===========================
                // Provider Information
                // ===========================

                .hospitalName(details.getHospitalName())
                .hospitalType(details.getHospitalType())
                .hospitalCity(details.getHospitalCity())
                .doctorName(details.getDoctorName())
                .doctorSpeciality(details.getDoctorSpeciality())

                // ===========================
                // Clinical Information
                // ===========================

                .primaryDiagnosis(details.getPrimaryDiagnosis())
                .secondaryDiagnosis(details.getSecondaryDiagnosis())
                .diagnosisCode(details.getDiagnosisCode())
                .requestedProcedure(details.getRequestedProcedure())
                .procedureCode(details.getProcedureCode())
                .procedureCategory(details.getProcedureCategory())
                .treatmentPlan(details.getTreatmentPlan())

                // ===========================
                // Medical Necessity
                // ===========================

                .medicalNecessityReason(details.getMedicalNecessityReason())
                .conservativeTreatmentAttempted(details.getConservativeTreatmentAttempted())
                .previousTreatments(details.getPreviousTreatments())

                // ===========================
                // Request Information
                // ===========================

                .requestType(details.getRequestType())
                .facilitySetting(details.getFacilitySetting())
                .requestedProcedureDate(details.getRequestedProcedureDate())

                // ===========================
                // Financial Information
                // ===========================

                .estimatedCost(details.getEstimatedCost())

                // ===========================
                // Supporting Documents
                // ===========================

                .documentsIncluded(details.getDocumentsIncluded())
                .missingDocuments(details.getMissingDocuments())

                .build();
    }

    public void populateAssessmentResult(PriorAuthorization priorAuthorization, RecommendationResult recommendationResult) {

        priorAuthorization.setRecommendation(recommendationResult.getRecommendation());

        priorAuthorization.setRecommendationReason(recommendationResult.getReason());

        priorAuthorization.setConfidence(recommendationResult.getConfidence());

        priorAuthorization.setObservations(recommendationResult.getObservations());

        priorAuthorization.setStatus(ClaimStatus.VALID);
    }
}