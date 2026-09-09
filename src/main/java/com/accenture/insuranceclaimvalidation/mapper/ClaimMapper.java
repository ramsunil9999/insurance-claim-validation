package com.accenture.insuranceclaimvalidation.mapper;

import org.springframework.stereotype.Component;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;
import com.accenture.insuranceclaimvalidation.entity.Claim;
import com.accenture.insuranceclaimvalidation.enums.ClaimStatus;

@Component
public class ClaimMapper {

    public Claim toEntity(ClaimDetails claimDetails) {

        return Claim.builder()

            .requestType(claimDetails.getRequestType())
            .priorAuthorizationId(claimDetails.getPriorAuthorizationId())

                // ===========================
                // Patient Information
                // ===========================

                .patientName(claimDetails.getPatientName())
                .age(claimDetails.getAge())
                .gender(claimDetails.getGender())

                // ===========================
                // Insurance Information
                // ===========================

                .policyNumber(claimDetails.getPolicyNumber())
                .memberId(claimDetails.getMemberId())
                .insurancePlan(claimDetails.getInsurancePlan())

                // ===========================
                // Hospital Information
                // ===========================

                .hospitalName(claimDetails.getHospitalName())
                .hospitalType(claimDetails.getHospitalType())
                .hospitalCity(claimDetails.getHospitalCity())

                // ===========================
                // Doctor Information
                // ===========================

                .doctorName(claimDetails.getDoctorName())
                .doctorSpeciality(claimDetails.getDoctorSpeciality())

                // ===========================
                // Medical Information
                // ===========================

                .diagnosis(claimDetails.getDiagnosis())
                .secondaryDiagnosis(claimDetails.getSecondaryDiagnosis())
                .symptoms(claimDetails.getSymptoms())
                .treatmentProvided(claimDetails.getTreatmentProvided())
                .procedurePerformed(claimDetails.getProcedurePerformed())
                .requestedProcedure(claimDetails.getRequestedProcedure())
                .procedureCategory(claimDetails.getProcedureCategory())
                .estimatedCost(claimDetails.getEstimatedCost())
                .requestedProcedureDate(claimDetails.getRequestedProcedureDate())
                .treatmentPlan(claimDetails.getTreatmentPlan())
                .referralDoctor(claimDetails.getReferralDoctor())
                .referringProvider(claimDetails.getReferringProvider())
                .policyStartDate(claimDetails.getPolicyStartDate())
                .missingDocuments(claimDetails.getMissingDocuments())
                .surgeryPerformed(claimDetails.getSurgeryPerformed())
                .icuRequired(claimDetails.getIcuRequired())

                // ===========================
                // Hospitalization
                // ===========================

                .admissionDate(claimDetails.getAdmissionDate())
                .dischargeDate(claimDetails.getDischargeDate())
                .lengthOfStay(claimDetails.getLengthOfStay())

                // ===========================
                // Financial Information
                // ===========================

                .claimAmount(claimDetails.getClaimAmount())
                .roomCharges(claimDetails.getRoomCharges())
                .medicineCharges(claimDetails.getMedicineCharges())
                .labCharges(claimDetails.getLabCharges())
                .procedureCharges(claimDetails.getProcedureCharges())
                .doctorConsultationCharges(claimDetails.getDoctorConsultationCharges())

                // ===========================
                // Additional Information
                // ===========================

                .emergencyAdmission(claimDetails.getEmergencyAdmission())
                .previousSimilarClaims(claimDetails.getPreviousSimilarClaims())

                .build();
    }

    public void populateAssessmentResult(Claim claim, RecommendationResult recommendationResult, boolean duplicate) {

        claim.setRecommendation(recommendationResult.getRecommendation());
        claim.setRecommendationReason(recommendationResult.getReason());
        claim.setConfidence(recommendationResult.getConfidence());
        claim.setObservations(recommendationResult.getObservations());
        claim.setRiskScore(recommendationResult.getRiskScore());
        claim.setRiskLevel(recommendationResult.getRiskLevel());
        claim.setMedicalNecessityScore(recommendationResult.getMedicalNecessityScore());
        claim.setCoverageStatus(recommendationResult.getCoverageStatus());
        claim.setWaitingPeriodSatisfied(recommendationResult.getWaitingPeriodSatisfied());
        claim.setDuplicateDetected(duplicate);
        claim.setPriorAuthorizationMatched(recommendationResult.getPriorAuthorizationMatched());

        claim.setStatus(duplicate
                ? ClaimStatus.DUPLICATE
                : recommendationResult.getRecommendation() == com.accenture.insuranceclaimvalidation.enums.Recommendation.MANUAL_REVIEW
                    ? ClaimStatus.MANUAL_REVIEW
                    : ClaimStatus.VALID);
    }
}