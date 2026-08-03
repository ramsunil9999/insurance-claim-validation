package com.accenture.insuranceclaimvalidation.service;

import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.dto.response.FileUploadResponse;

public interface ClaimService {

    FileUploadResponse uploadClaim(MultipartFile file);
}