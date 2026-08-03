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
@Document(collection = "claims")
public class Claim {

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