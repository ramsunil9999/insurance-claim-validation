package com.accenture.insuranceclaimvalidation.service;

import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.dto.response.FileUploadResponse;
import com.accenture.insuranceclaimvalidation.enums.RequestType;

public interface ClaimService {

    FileUploadResponse uploadClaim(MultipartFile file);

    FileUploadResponse uploadClaim(MultipartFile file, RequestType requestType);
}