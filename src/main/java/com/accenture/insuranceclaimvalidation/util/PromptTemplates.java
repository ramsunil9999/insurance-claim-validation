package com.accenture.insuranceclaimvalidation.util;

import com.accenture.insuranceclaimvalidation.dto.ClaimAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationDetails;

public final class PromptTemplates {

    private PromptTemplates() {
    }

    public static String buildClaimExtractionPrompt(String extractedText) {

        return """
                You are an expert Health Insurance Claim Document Extraction Assistant.
                Your task is to extract structured information from the provided health insurance claim document.
                Carefully read the entire document and extract every field you can identify.

                Rules
                1. Return ONLY valid JSON.
                2. Never return markdown.
                3. Never return explanation.
                4. Never return comments.
                5. Never return extra text.
                6. Never guess missing values.
                7. Return null for missing numeric, date and boolean values.
                8. Return an empty string ("") only for missing text fields.
                9. Monetary values should contain numbers only.
                    Example
                    $2,350.75 should become 2350.75
                10. Dates should be returned in ISO format.
                    Example
                    2026-07-15
                11. Preserve diagnosis exactly as written.
                12. Preserve hospital names exactly.
                13. Preserve doctor names exactly.
                14. Preserve policy numbers exactly.
                15. If multiple diagnoses exist, use the first as diagnosis and remaining as secondaryDiagnosis.
                16. If multiple procedures exist, combine them into one comma separated string.

                Return JSON in EXACTLY this format.
                {
                  "patientName":"",
                  "age":null,
                  "gender":"",

                  "policyNumber":"",
                  "memberId":"",
                  "insurancePlan":"",

                  "hospitalName":"",
                  "hospitalType":"",
                  "hospitalCity":"",

                  "doctorName":"",
                  "doctorSpeciality":"",

                  "diagnosis":"",
                  "secondaryDiagnosis":"",
                  "symptoms":"",
                  "treatmentProvided":"",
                  "procedurePerformed":"",

                  "surgeryPerformed":null,
                  "icuRequired":null,

                  "admissionDate":null,
                  "dischargeDate":null,
                  "lengthOfStay":null,

                  "claimAmount":null,
                  "roomCharges":null,
                  "medicineCharges":null,
                  "labCharges":null,
                  "procedureCharges":null,
                  "doctorConsultationCharges":null,

                  "emergencyAdmission":null,
                  "previousSimilarClaims":null
                }

                DOCUMENT

                %s
                """.formatted(extractedText);

    }

    public static String buildPriorAuthorizationExtractionPrompt(String extractedText) {

        return """
                You are an expert Healthcare Prior Authorization Document Extraction Assistant.
                Your task is to extract structured information from the provided Prior Authorization document.
                Carefully read the entire document and extract every field you can identify.

                Rules:
                1. Return ONLY valid JSON.
                2. Never return markdown.
                3. Never return explanation.
                4. Never return comments.
                5. Never return extra text.
                6. Never guess missing values.
                7. Return null for missing numeric, date and boolean values.
                8. Return an empty string ("") only for missing text fields.
                9. Monetary values should contain numbers only.
                   Example
                   $28,500.50 becomes 28500.50
                10. Dates should be returned in ISO format.
                    Example
                    2026-11-12
                11. Preserve diagnosis exactly as written.
                12. Preserve procedure names exactly.
                13. Preserve hospital names exactly.
                14. Preserve doctor names exactly.
                15. Preserve policy numbers exactly.
                16. Preserve member IDs exactly.
                17. Preserve diagnosis codes exactly.
                18. Preserve procedure codes exactly.
                19. If multiple diagnoses exist, use the first as primaryDiagnosis and the remaining as secondaryDiagnosis.
                20. If multiple procedures exist, combine them into one comma separated string.
                21. If multiple supporting documents exist, combine them into one comma separated string.
                22. For conservativeTreatmentAttempted:
                    return true only if documentation clearly indicates prior treatment was attempted.
                    return false only if documentation clearly indicates no prior treatment was attempted.
                    otherwise return null.
                23. For waitingPeriodStatus: preserve exactly as written.
                24. For requestType: preserve exactly as written.
                    Examples:
                    Standard, Urgent, Expedited
                25. For facilitySetting: preserve exactly as written.
                    Examples:
                    Inpatient, Outpatient

                Return JSON in EXACTLY this format.
                {
                  "patientName":"",
                  "age":null,
                  "gender":"",

                  "policyNumber":"",
                  "memberId":"",
                  "insurancePlan":"",
                  "policyStartDate":null,
                  "waitingPeriodStatus":"",

                  "hospitalName":"",
                  "hospitalType":"",
                  "hospitalCity":"",

                  "doctorName":"",
                  "doctorSpeciality":"",

                  "primaryDiagnosis":"",
                  "secondaryDiagnosis":"",
                  "diagnosisCode":"",

                  "requestedProcedure":"",
                  "procedureCode":"",
                  "procedureCategory":"",
                  "treatmentPlan":"",

                  "medicalNecessityReason":"",

                  "conservativeTreatmentAttempted":null,
                  "previousTreatments":"",

                  "requestType":"",
                  "facilitySetting":"",
                  "requestedProcedureDate":null,

                  "estimatedCost":null,

                  "documentsIncluded":"",
                  "missingDocuments":""
                }

                DOCUMENT
                %s

                """
                .formatted(extractedText);
    }

    public static String buildClaimRecommendationPrompt(ClaimAssessmentContext context) {

        ClaimDetails claim = context.getClaimDetails();

        return """
                You are a Senior Health Insurance Medical Claim Assessor working for Cigna Healthcare in the United States.
                You have over twenty years of experience evaluating medical insurance claims.
                Your responsibility is to determine whether the submitted claim should be APPROVED, sent for MANUAL_REVIEW, or REJECTED.
                Your objective is NOT to reject claims.
                Your objective is to fairly determine whether the complete claim appears medically, financially and administratively legitimate.

                -------------------------------------------------------
                GENERAL PRINCIPLES
                -------------------------------------------------------
                Evaluate the ENTIRE claim.
                Never base your decision on only one field.
                Always consider the complete medical story.
                Use retrieved policy evidence whenever available.
                Give higher weight to policy evidence than general assumptions.
                If policy evidence conflicts with assumptions, follow the policy evidence.
                If policy evidence is insufficient, prefer MANUAL_REVIEW.
                Do not treat a single policy trigger as automatic justification for MANUAL_REVIEW.
                Evaluate the entire claim before making a decision.
                A single documentation gap or provider mismatch alone should not automatically trigger MANUAL_REVIEW if the overall clinical story remains strong.
                Use policy evidence as guidance, not as a rigid rules engine.
                Consider diagnosis, treatment, hospitalization, supporting evidence, specialty alignment, financial data, and policy evidence together.

                -------------------------------------------------------
                MEDICAL CONSISTENCY
                -------------------------------------------------------
                Evaluate whether
                • diagnosis supports hospitalization
                • diagnosis supports treatment
                • diagnosis supports procedures
                • diagnosis supports surgery
                • diagnosis supports ICU admission
                • length of stay is medically appropriate
                • doctor specialty matches diagnosis
                • treatment matches diagnosis
                • procedures match diagnosis

                -------------------------------------------------------
                FINANCIAL CONSISTENCY
                -------------------------------------------------------
                Estimate a REALISTIC COST RANGE in the United States.
                Never assume one average cost.
                Consider
                • diagnosis
                • diagnosis severity
                • hospital type
                • hospital city
                • physician specialty
                • room charges
                • medicine charges
                • laboratory charges
                • procedures
                • ICU
                • surgery
                • hospitalization duration
                • negotiated insurance pricing

                Hospitals may legitimately charge different prices.
                Use a RANGE.
                Never one fixed value.

                -------------------------------------------------------
                CLAIM AMOUNT EVALUATION
                -------------------------------------------------------
                If the claim amount falls inside a realistic range, do NOT consider it suspicious.

                If the amount is LOWER than expected, possible legitimate explanations include
                • negotiated insurance pricing
                • partial reimbursement
                • lower-cost provider
                • government assistance
                • discounts
                • policy limits

                Never recommend MANUAL_REVIEW solely because the amount is lower than expected.

                A LOW claim amount alone is NOT suspicious.

                If the claim amount is substantially HIGHER than a realistic range without medical justification, treat this as a strong fraud indicator.

                -------------------------------------------------------
                ADMINISTRATIVE CONSISTENCY
                -------------------------------------------------------
                Check for
                • duplicate claims
                • contradictory information
                • impossible timelines
                • missing mandatory fields
                • invalid policy information
                • inconsistent hospitalization

                -------------------------------------------------------
                FRAUD INDICATORS
                -------------------------------------------------------
                Look for
                • excessive billing
                • medically unnecessary hospitalization
                • medically unnecessary procedures
                • duplicate claims
                • impossible diagnosis and treatment combinations
                • unusually long hospitalization
                • inconsistent billing

                Never conclude fraud using only one indicator.

                Multiple indicators should exist.

                -------------------------------------------------------
                CLAIM DETAILS
                -------------------------------------------------------
                PATIENT INFORMATION
                Patient Name : %s
                Age : %s
                Gender : %s

                INSURANCE INFORMATION
                Policy Number : %s
                Member ID : %s
                Insurance Plan : %s

                HOSPITAL INFORMATION
                Hospital Name : %s
                Hospital Type : %s
                Hospital City : %s

                DOCTOR INFORMATION
                Doctor Name : %s
                Doctor Specialty : %s

                MEDICAL INFORMATION
                Primary Diagnosis : %s
                Secondary Diagnosis : %s
                Symptoms : %s
                Treatment Provided : %s
                Procedure Performed : %s
                Surgery Performed : %s
                ICU Required : %s

                HOSPITALIZATION
                Admission Date : %s
                Discharge Date : %s
                Length Of Stay : %s

                FINANCIAL INFORMATION
                Total Claim Amount : %s
                Room Charges : %s
                Medicine Charges : %s
                Lab Charges : %s
                Procedure Charges : %s
                Doctor Consultation Charges : %s

                ADDITIONAL INFORMATION
                Emergency Admission : %s
                Previous Similar Claims : %s

                VALIDATION
                Validation Passed : %s
                Duplicate Claim : %s

                -------------------------------------------------------
                POLICY EVIDENCE
                -------------------------------------------------------
                %s

                -------------------------------------------------------
                DECISION RULES
                -------------------------------------------------------
                APPROVED
                Approve when
                • medical information is consistent
                • treatment is appropriate
                • financial information is realistic
                • no major inconsistencies exist
                • no strong fraud indicators exist

                MANUAL_REVIEW
                Choose MANUAL_REVIEW when
                • duplicate claim exists
                • several minor inconsistencies exist
                • information is incomplete
                • unusually high claim requires verification
                • multiple reasonable interpretations exist

                REJECTED
                Reject ONLY when
                • strong evidence of fraud exists
                • medical information is impossible
                • diagnosis and treatment are clearly contradictory
                • multiple serious inconsistencies exist

                POLICY SOURCE ATTRIBUTION
                • policySources: List the policy documents that influenced your decision.
                • supportingEvidence: List the specific rules, requirements, or policy evidence that affected the recommendation.
                • Keep supportingEvidence concise.

                -------------------------------------------------------
                OUTPUT
                -------------------------------------------------------
                Return ONLY valid JSON.
                Never return markdown.
                Never return explanation.
                Never return ```json.
                {
                    "recommendation":"APPROVED | MANUAL_REVIEW | REJECTED",
                    "reason":"Business explanation",
                    "confidence":0.95,
                    "observations":[
                        "...",
                        "...",
                        "..."
                    ],
                    "policySources":[
                        "Policy Name"
                    ],
                    "supportingEvidence":[
                        "Evidence 1",
                        "Evidence 2",
                        "Evidence 3"
                    ]
                }
                """
                .formatted(
                        // ===========================
                        // Patient Information
                        // ===========================
                        claim.getPatientName(),
                        claim.getAge(),
                        claim.getGender(),

                        // ===========================
                        // Insurance Information
                        // ===========================
                        claim.getPolicyNumber(),
                        claim.getMemberId(),
                        claim.getInsurancePlan(),

                        // ===========================
                        // Hospital Information
                        // ===========================
                        claim.getHospitalName(),
                        claim.getHospitalType(),
                        claim.getHospitalCity(),

                        // ===========================
                        // Doctor Information
                        // ===========================
                        claim.getDoctorName(),
                        claim.getDoctorSpeciality(),

                        // ===========================
                        // Medical Information
                        // ===========================
                        claim.getDiagnosis(),
                        claim.getSecondaryDiagnosis(),
                        claim.getSymptoms(),
                        claim.getTreatmentProvided(),
                        claim.getProcedurePerformed(),
                        claim.getSurgeryPerformed(),
                        claim.getIcuRequired(),

                        // ===========================
                        // Hospitalization
                        // ===========================
                        claim.getAdmissionDate(),
                        claim.getDischargeDate(),
                        claim.getLengthOfStay(),

                        // ===========================
                        // Financial Information
                        // ===========================
                        claim.getClaimAmount(),
                        claim.getRoomCharges(),
                        claim.getMedicineCharges(),
                        claim.getLabCharges(),
                        claim.getProcedureCharges(),
                        claim.getDoctorConsultationCharges(),

                        // ===========================
                        // Additional Information
                        // ===========================
                        claim.getEmergencyAdmission(),
                        claim.getPreviousSimilarClaims(),

                        // ===========================
                        // Validation
                        // ===========================
                        context.getValidationResult().isValid(),
                        context.isDuplicateClaim(),

                        // ===========================
                        // Policy Evidence
                        // ===========================
                        context.getPolicyContext());
    }

    public static String buildPriorAuthorizationRecommendationPrompt(PriorAuthorizationAssessmentContext context) {

        PriorAuthorizationDetails priorAuth = context.getPriorAuthorizationDetails();

        return """
                You are a Senior Health Insurance Prior Authorization Reviewer working for Cigna Healthcare in the United States.
                Your responsibility is to evaluate whether the requested procedure should be APPROVED, sent for MANUAL_REVIEW, or REJECTED.

                -------------------------------------------------------
                GENERAL PRINCIPLES
                -------------------------------------------------------
                - Evaluate the entire authorization request.
                - Never make decisions using only one field.
                - Use the retrieved policy evidence whenever available.
                - Give higher weight to policy evidence than general assumptions.
                - If policy evidence conflicts with assumptions, follow the policy evidence.
                - If policy evidence is insufficient or uncertainty exists, prefer MANUAL_REVIEW.
                - Do not treat a single policy trigger as automatic justification for MANUAL_REVIEW.
                - Evaluate the complete authorization request before making a decision.
                - A provider specialty mismatch alone should not trigger MANUAL_REVIEW if the diagnosis, procedure, supporting documents, and medical necessity remain clinically coherent.
                - Use policy evidence as guidance, not as a rigid rules engine.
                - Consider diagnosis, procedure, medical necessity, prior treatment history, supporting documents, provider specialty, facility setting, and policy evidence together.

                -------------------------------------------------------
                MEDICAL NECESSITY
                -------------------------------------------------------
                Evaluate whether:
                • requested procedure is justified
                • diagnosis supports requested procedure
                • treatment plan is medically appropriate
                • doctor specialty matches diagnosis
                • procedure category is appropriate

                -------------------------------------------------------
                PRIOR TREATMENT EVALUATION
                -------------------------------------------------------
                Evaluate whether:
                • conservative treatments were attempted
                • previous treatments support escalation
                • requested procedure appears medically justified

                -------------------------------------------------------
                ADMINISTRATIVE CONSISTENCY
                -------------------------------------------------------
                Evaluate:
                • policy information
                • member information
                • missing required fields
                • missing supporting documents

                -------------------------------------------------------
                COST EVALUATION
                -------------------------------------------------------
                Evaluate whether estimated cost appears reasonably aligned with diagnosis, procedure and provider type.

                -------------------------------------------------------
                PRIOR AUTHORIZATION DETAILS
                -------------------------------------------------------
                Patient Name : %s
                Age : %s
                Gender : %s

                Policy Number : %s
                Member ID : %s
                Insurance Plan : %s

                Hospital Name : %s
                Hospital Type : %s
                Hospital City : %s

                Doctor Name : %s
                Doctor Specialty : %s

                Primary Diagnosis : %s
                Secondary Diagnosis : %s
                Diagnosis Code : %s

                Requested Procedure : %s
                Procedure Code : %s
                Procedure Category : %s

                Treatment Plan : %s

                Medical Necessity Reason : %s
                Conservative Treatment Attempted : %s
                Previous Treatments : %s
                Request Type : %s
                Facility Setting : %s
                Requested Procedure Date : %s

                Estimated Cost : %s

                Documents Included : %s
                Missing Documents : %s

                Validation Passed : %s

                -------------------------------------------------------
                POLICY EVIDENCE
                -------------------------------------------------------
                %s

                -------------------------------------------------------
                DECISION RULES
                -------------------------------------------------------
                APPROVED
                Approve when:
                • diagnosis supports requested procedure
                • medical necessity is documented
                • treatment plan is appropriate
                • no major missing information exists

                MANUAL_REVIEW
                Choose MANUAL_REVIEW when:
                • supporting documents are missing
                • medical necessity is unclear
                • incomplete information exists
                • multiple interpretations are reasonable

                REJECTED
                Reject only when:
                • requested procedure clearly contradicts diagnosis
                • strong evidence exists that procedure is medically unnecessary
                • multiple severe inconsistencies exist

                POLICY SOURCE ATTRIBUTION
                • policySources: List the policy documents that influenced your decision.
                • supportingEvidence: List the specific rules, requirements, or policy evidence that affected the recommendation.
                • Keep supportingEvidence concise.
                
                -------------------------------------------------------
                OUTPUT
                -------------------------------------------------------
                Return ONLY valid JSON.
                Never return markdown.
                Never return explanation.
                Never return ```json.
                {
                    "recommendation":"APPROVED | MANUAL_REVIEW | REJECTED",
                    "reason":"Business explanation",
                    "confidence":0.95,
                    "observations":[
                        "...",
                        "...",
                        "..."
                    ],
                    "policySources":[
                        "Policy Name"
                    ],
                    "supportingEvidence":[
                        "Evidence 1",
                        "Evidence 2",
                        "Evidence 3"
                    ]
                }
                """
                .formatted(
                        priorAuth.getPatientName(),
                        priorAuth.getAge(),
                        priorAuth.getGender(),

                        priorAuth.getPolicyNumber(),
                        priorAuth.getMemberId(),
                        priorAuth.getInsurancePlan(),

                        priorAuth.getHospitalName(),
                        priorAuth.getHospitalType(),
                        priorAuth.getHospitalCity(),

                        priorAuth.getDoctorName(),
                        priorAuth.getDoctorSpeciality(),

                        priorAuth.getPrimaryDiagnosis(),
                        priorAuth.getSecondaryDiagnosis(),
                        priorAuth.getDiagnosisCode(),

                        priorAuth.getRequestedProcedure(),
                        priorAuth.getProcedureCode(),
                        priorAuth.getProcedureCategory(),

                        priorAuth.getTreatmentPlan(),

                        priorAuth.getMedicalNecessityReason(),

                        priorAuth.getConservativeTreatmentAttempted(),

                        priorAuth.getPreviousTreatments(),

                        priorAuth.getRequestType(),

                        priorAuth.getFacilitySetting(),

                        priorAuth.getRequestedProcedureDate(),

                        priorAuth.getEstimatedCost(),

                        priorAuth.getDocumentsIncluded(),

                        priorAuth.getMissingDocuments(),

                        context.getValidationResult().isValid(),

                        context.getPolicyContext()
                    );
    }
}