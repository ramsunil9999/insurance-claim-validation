package com.accenture.insuranceclaimvalidation.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorAuthorizationDetails {

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
}
