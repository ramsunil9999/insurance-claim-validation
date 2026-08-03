package com.accenture.insuranceclaimvalidation.dto.response;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.RecommendationResult;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private String claimId;

    private String fileName;

    private String contentType;

    private long size;

    private String message;

    private String extractedText;

    private ClaimDetails claimDetails;

    private ValidationResult validationResult;

    private RecommendationResult recommendationResult;

}