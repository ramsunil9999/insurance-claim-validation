package com.accenture.insuranceclaimvalidation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.dto.ClaimAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;
import com.accenture.insuranceclaimvalidation.dto.response.FileUploadResponse;
import com.accenture.insuranceclaimvalidation.entity.Claim;
import com.accenture.insuranceclaimvalidation.exception.InvalidFileException;
import com.accenture.insuranceclaimvalidation.mapper.ClaimMapper;
import com.accenture.insuranceclaimvalidation.repository.ClaimRepository;
import com.accenture.insuranceclaimvalidation.service.AIRecommendationService;
import com.accenture.insuranceclaimvalidation.service.AIService;
import com.accenture.insuranceclaimvalidation.service.ClaimService;
import com.accenture.insuranceclaimvalidation.service.DocumentProcessingService;
import com.accenture.insuranceclaimvalidation.service.validation.ClaimValidationService;
import com.accenture.insuranceclaimvalidation.enums.RequestType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final DocumentProcessingService documentProcessingService;
    private final AIService aiService;
    private final ClaimValidationService claimValidationService;
    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final AIRecommendationService aiRecommendationService;

    @Override
    public FileUploadResponse uploadClaim(MultipartFile file) {
        return uploadClaim(file, RequestType.CLAIM);
    }

    @Override
    public FileUploadResponse uploadClaim(MultipartFile file, RequestType expectedRequestType) {

        if (file.isEmpty()) {
            log.error("Uploaded file is empty.");
            throw new InvalidFileException("Uploaded file is empty.");
        }

        String extractedText = documentProcessingService.processDocument(file);

        log.info("Document text extracted successfully.");

        return processClaimInternal(file, extractedText, expectedRequestType);
    }

    private FileUploadResponse processClaimInternal(MultipartFile file, String extractedText,
            RequestType expectedRequestType) {

        ClaimDetails claimDetails = aiService.extractClaimDetails(extractedText);

        if (claimDetails.getRequestType() == null) {
            claimDetails.setRequestType(expectedRequestType);
        }

        if (claimDetails.getRequestType() != expectedRequestType) {
            log.warn("Document type mismatch. Expected={}, detected={}", expectedRequestType, claimDetails.getRequestType());
            return FileUploadResponse.builder()
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .message("Document type mismatch. This document is classified as "
                    + claimDetails.getRequestType() + ", but the selected section is " + expectedRequestType + ".")
                .extractedText(extractedText)
                .claimDetails(claimDetails)
                .validationResult(ValidationResult.builder()
                    .valid(false)
                    .errors(java.util.List.of("Upload a " + expectedRequestType + " document in this section."))
                    .build())
                .build();
        }

        log.info("Claim details extracted successfully using AI.");

        ValidationResult validationResult = claimValidationService.validate(claimDetails);

        if (!validationResult.isValid()) {

            log.warn("Claim validation failed. Returning validation errors.");

            return FileUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .message("Claim validation failed.")
                    .extractedText(extractedText)
                    .claimDetails(claimDetails)
                    .validationResult(validationResult)
                    .build();
        }

        boolean duplicate = claimRepository.existsByPolicyNumberAndMemberIdAndDiagnosisAndAdmissionDate(
                claimDetails.getPolicyNumber(),
                claimDetails.getMemberId(),
                claimDetails.getDiagnosis(),
                claimDetails.getAdmissionDate());

        boolean priorAuthorizationMatched = true;
        if (claimDetails.getRequestType() == RequestType.CLAIM && claimDetails.getPriorAuthorizationId() != null && !claimDetails.getPriorAuthorizationId().isBlank()) {
            Claim priorAuthorization = claimRepository.findFirstByRequestTypeAndPriorAuthorizationId(
                RequestType.PRIOR_AUTH, claimDetails.getPriorAuthorizationId()).orElse(null);
            priorAuthorizationMatched = priorAuthorization != null
                && sameProcedure(priorAuthorization.getRequestedProcedure(), claimDetails.getProcedurePerformed());
            if (!priorAuthorizationMatched) {
                validationResult.getRiskFactors().add("Claim does not match the approved prior authorization.");
            }
        }
        validationResult.setDuplicateDetected(duplicate);
        validationResult.setPriorAuthorizationMatched(priorAuthorizationMatched);

        ClaimAssessmentContext context = ClaimAssessmentContext.builder()
                .claimDetails(claimDetails)
                .validationResult(validationResult)
                .duplicateClaim(duplicate)
                .build();

        RecommendationResult recommendationResult = aiRecommendationService.recommendClaim(context);
        applyAssessment(recommendationResult, validationResult, duplicate, priorAuthorizationMatched);
        if (duplicate || !priorAuthorizationMatched) {
            recommendationResult.setRecommendation(com.accenture.insuranceclaimvalidation.enums.Recommendation.MANUAL_REVIEW);
        }

        log.info("AI recommendation generated: {}", recommendationResult.getRecommendation());

        Claim claim = claimMapper.toEntity(claimDetails);
        claimMapper.populateAssessmentResult(claim, recommendationResult, duplicate);

        String responseMessage;

        if (duplicate) {
            responseMessage = "Duplicate claim detected. Claim has been marked as duplicate.";
            log.warn("Duplicate claim detected for policy number: {}", claimDetails.getPolicyNumber());
        } else {
            responseMessage = "Claim processed successfully.";
            log.info("Claim is valid and unique.");
        }

        Claim savedClaim = claimRepository.save(claim);

        log.info("Claim saved successfully with ID: {}", savedClaim.getId());

        return FileUploadResponse.builder()
                .claimId(savedClaim.getId())
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .message(responseMessage)
                .extractedText(extractedText)
                .claimDetails(claimDetails)
                .validationResult(validationResult)
                .recommendationResult(recommendationResult)
                .build();
    }

    private boolean sameProcedure(String approved, String actual) {
        return approved != null && actual != null
                && (actual.toLowerCase().contains(approved.toLowerCase())
                || approved.toLowerCase().contains(actual.toLowerCase()));
    }

    private void applyAssessment(RecommendationResult recommendationResult, ValidationResult validationResult,
            boolean duplicate, boolean priorAuthorizationMatched) {
        if (recommendationResult.getRiskScore() != null) {
            validationResult.setRiskScore(Math.max(0, Math.min(100, recommendationResult.getRiskScore())));
        }
        if (recommendationResult.getRiskLevel() != null) {
            validationResult.setRiskLevel(recommendationResult.getRiskLevel());
        }
        if (recommendationResult.getMedicalNecessityScore() != null) {
            validationResult.setMedicalNecessityScore(recommendationResult.getMedicalNecessityScore());
        }
        if (recommendationResult.getCoverageStatus() != null) {
            validationResult.setCoverageStatus(recommendationResult.getCoverageStatus());
        }
        if (recommendationResult.getWaitingPeriodSatisfied() != null) {
            validationResult.setWaitingPeriodSatisfied(recommendationResult.getWaitingPeriodSatisfied());
        }
        if (recommendationResult.getProcedureDiagnosisValid() != null) {
            validationResult.setProcedureDiagnosisValid(recommendationResult.getProcedureDiagnosisValid());
        }
        if (recommendationResult.getDoctorSpecialtyValid() != null) {
            validationResult.setDoctorSpecialtyValid(recommendationResult.getDoctorSpecialtyValid());
        }
        if (recommendationResult.getHospitalCapabilityValid() != null) {
            validationResult.setHospitalCapabilityValid(recommendationResult.getHospitalCapabilityValid());
        }
        if (recommendationResult.getRiskFactors() != null) {
            java.util.List<String> riskFactors = new java.util.ArrayList<>(validationResult.getRiskFactors());
            riskFactors.addAll(recommendationResult.getRiskFactors());
            validationResult.setRiskFactors(riskFactors.stream().distinct().toList());
        }
        recommendationResult.setRiskScore(validationResult.getRiskScore());
        recommendationResult.setRiskLevel(validationResult.getRiskLevel());
        recommendationResult.setMedicalNecessityScore(validationResult.getMedicalNecessityScore());
        recommendationResult.setCoverageStatus(validationResult.getCoverageStatus());
        recommendationResult.setWaitingPeriodSatisfied(validationResult.getWaitingPeriodSatisfied());
        recommendationResult.setDuplicateDetected(duplicate);
        recommendationResult.setPriorAuthorizationMatched(priorAuthorizationMatched);
    }
}