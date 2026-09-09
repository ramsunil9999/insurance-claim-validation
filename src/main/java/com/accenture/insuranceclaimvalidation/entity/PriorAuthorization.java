package com.accenture.insuranceclaimvalidation.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.accenture.insuranceclaimvalidation.enums.ClaimStatus;
import com.accenture.insuranceclaimvalidation.enums.Recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "priorAuthorizations")
public class PriorAuthorization {

    @Id
    private String id;

    // ===========================
    // Patient Information
    // ===========================

    private String patientName;
    private Integer age;
    private String gender;

    // ===========================
    // Insurance Information
    // ===========================

    private String policyNumber;
    private String memberId;
    private String insurancePlan;
    private LocalDate policyStartDate;
    private String waitingPeriodStatus;

    // ===========================
    // Provider Information
    // ===========================

    private String hospitalName;
    private String hospitalType;
    private String hospitalCity;
    private String doctorName;
    private String doctorSpeciality;

    // ===========================
    // Clinical Information
    // ===========================

    private String primaryDiagnosis;
    private String secondaryDiagnosis;
    private String diagnosisCode;
    private String requestedProcedure;
    private String procedureCode;
    private String procedureCategory;
    private String treatmentPlan;

    // ===========================
    // Medical Necessity
    // ===========================

    private String medicalNecessityReason;
    private Boolean conservativeTreatmentAttempted;
    private String previousTreatments;

    // ===========================
    // Request Information
    // ===========================

    private String requestType;
    private String facilitySetting;
    private LocalDate requestedProcedureDate;

    // ===========================
    // Financial Information
    // ===========================

    private Double estimatedCost;

    // ===========================
    // Supporting Documents
    // ===========================

    private String documentsIncluded;
    private String missingDocuments;

    // ===========================
    // Processing Status
    // ===========================

    private ClaimStatus status;

    // ===========================
    // AI Recommendation
    // ===========================

    private Recommendation recommendation;
    private String recommendationReason;
    private Double confidence;
    private List<String> observations;
}