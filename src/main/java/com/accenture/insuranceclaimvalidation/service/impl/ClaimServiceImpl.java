package com.accenture.insuranceclaimvalidation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.dto.ClaimAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationDetails;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;
import com.accenture.insuranceclaimvalidation.dto.response.FileUploadResponse;
import com.accenture.insuranceclaimvalidation.entity.Claim;
import com.accenture.insuranceclaimvalidation.entity.PriorAuthorization;
import com.accenture.insuranceclaimvalidation.enums.DocumentType;
import com.accenture.insuranceclaimvalidation.exception.InvalidFileException;
import com.accenture.insuranceclaimvalidation.mapper.ClaimMapper;
import com.accenture.insuranceclaimvalidation.mapper.PriorAuthorizationMapper;
import com.accenture.insuranceclaimvalidation.repository.ClaimRepository;
import com.accenture.insuranceclaimvalidation.repository.PriorAuthorizationRepository;
import com.accenture.insuranceclaimvalidation.service.AIRecommendationService;
import com.accenture.insuranceclaimvalidation.service.AIService;
import com.accenture.insuranceclaimvalidation.service.ClaimService;
import com.accenture.insuranceclaimvalidation.service.DocumentClassificationService;
import com.accenture.insuranceclaimvalidation.service.DocumentProcessingService;
import com.accenture.insuranceclaimvalidation.service.validation.ClaimValidationService;
import com.accenture.insuranceclaimvalidation.service.validation.PriorAuthorizationValidationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final DocumentProcessingService documentProcessingService;
    private final AIService aiService;
    private final ClaimValidationService claimValidationService;
    private final PriorAuthorizationValidationService priorAuthorizationValidationService;
    private final ClaimRepository claimRepository;
    private final PriorAuthorizationRepository priorAuthorizationRepository;
    private final ClaimMapper claimMapper;
    private final PriorAuthorizationMapper priorAuthorizationMapper;
    private final AIRecommendationService aiRecommendationService;
    private final DocumentClassificationService documentClassificationService;

    @Override
    public FileUploadResponse uploadClaim(MultipartFile file) {

        if (file.isEmpty()) {
            log.error("Uploaded file is empty.");
            throw new InvalidFileException("Uploaded file is empty.");
        }

        String extractedText = documentProcessingService.processDocument(file);

        log.info("Document text extracted successfully.");

        DocumentType documentType = documentClassificationService.classify(extractedText);

        log.info("Document classified as {}.", documentType);

        if (documentType == DocumentType.PRIOR_AUTHORIZATION) {
            return processPriorAuthorizationInternal(file, extractedText);
        }

        return processClaimInternal(file, extractedText);
    }

    private FileUploadResponse processClaimInternal(MultipartFile file, String extractedText) {

        ClaimDetails claimDetails = aiService.extractClaimDetails(extractedText);

        log.info("Claim details extracted successfully using AI.");

        ValidationResult validationResult = claimValidationService.validate(claimDetails);

        if (!validationResult.isValid()) {

            log.warn("Claim validation failed. Returning validation errors.");

            return FileUploadResponse.builder()
                    .documentType("CLAIM")
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

        ClaimAssessmentContext context = ClaimAssessmentContext.builder()
                .claimDetails(claimDetails)
                .validationResult(validationResult)
                .duplicateClaim(duplicate)
                .build();

        RecommendationResult recommendationResult = aiRecommendationService.recommendClaim(context);

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
                .documentType("CLAIM")
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

    private FileUploadResponse processPriorAuthorizationInternal(MultipartFile file, String extractedText) {

        PriorAuthorizationDetails priorAuthorizationDetails = aiService.extractPriorAuthorizationDetails(extractedText);

        log.info("Prior Authorization details extracted successfully.");

        ValidationResult validationResult = priorAuthorizationValidationService.validate(priorAuthorizationDetails);

        if (!validationResult.isValid()) {

            log.warn("Prior Authorization validation failed.");

            return FileUploadResponse.builder()
                    .documentType("PRIOR AUTHORIZATION")
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .message("Prior Authorization validation failed.")
                    .extractedText(extractedText)
                    .priorAuthorizationDetails(priorAuthorizationDetails)
                    .validationResult(validationResult)
                    .build();
        }

        PriorAuthorizationAssessmentContext context = PriorAuthorizationAssessmentContext.builder()
                .priorAuthorizationDetails(priorAuthorizationDetails)
                .validationResult(validationResult)
                .build();
        
        RecommendationResult recommendationResult = aiRecommendationService.recommendPriorAuthorization(context);

        log.info("AI Prior Authorization recommendation generated: {}", recommendationResult.getRecommendation());

        PriorAuthorization priorAuthorization = priorAuthorizationMapper.toEntity(priorAuthorizationDetails);
        priorAuthorizationMapper.populateAssessmentResult(priorAuthorization, recommendationResult);
        
        PriorAuthorization savedPriorAuthorization = priorAuthorizationRepository.save(priorAuthorization);
        log.info("Prior Authorization saved successfully with ID: {}", savedPriorAuthorization.getId());

        return FileUploadResponse.builder()
                .claimId(savedPriorAuthorization.getId())
                .documentType("PRIOR AUTHORIZATION")
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .message("Prior Authorization processed successfully.")
                .extractedText(extractedText)
                .priorAuthorizationDetails(priorAuthorizationDetails)
                .validationResult(validationResult)
                .recommendationResult(recommendationResult)
                .build();
    }
}