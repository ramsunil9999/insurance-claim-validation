package com.accenture.insuranceclaimvalidation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.dto.ClaimAssessmentContext;
import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;
import com.accenture.insuranceclaimvalidation.dto.request.ClaimRequest;
import com.accenture.insuranceclaimvalidation.dto.response.ClaimResponse;
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

        if (file.isEmpty()) {
            log.error("Uploaded file is empty.");
            throw new InvalidFileException("Uploaded file is empty.");
        }

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            log.error("Invalid file type. Only PDF files are allowed.");
            throw new InvalidFileException("Only PDF files are allowed.");
        }

        String extractedText = documentProcessingService.processDocument(file);

        log.info("Document text extracted successfully.");

        return processClaimInternal(file, extractedText);
    }

    private FileUploadResponse processClaimInternal(MultipartFile file, String extractedText) {

        ClaimDetails claimDetails = aiService.extractClaimDetails(extractedText);

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
}