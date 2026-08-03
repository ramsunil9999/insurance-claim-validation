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
public class ClaimDetails {

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

    // ===========================
    // Hospital Information
    // ===========================

    private String hospitalName;

    private String hospitalType;

    private String hospitalCity;

    // ===========================
    // Doctor Information
    // ===========================

    private String doctorName;

    private String doctorSpeciality;

    // ===========================
    // Medical Information
    // ===========================

    private String diagnosis;

    private String secondaryDiagnosis;

    private String symptoms;

    private String treatmentProvided;

    private String procedurePerformed;

    private Boolean surgeryPerformed;

    private Boolean icuRequired;

    // ===========================
    // Hospitalization
    // ===========================

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private Integer lengthOfStay;

    // ===========================
    // Financial Information
    // ===========================

    private Double claimAmount;

    private Double roomCharges;

    private Double medicineCharges;

    private Double labCharges;

    private Double procedureCharges;

    private Double doctorConsultationCharges;

    // ===========================
    // Additional Information
    // ===========================

    private Boolean emergencyAdmission;

    private Boolean previousSimilarClaims;

}