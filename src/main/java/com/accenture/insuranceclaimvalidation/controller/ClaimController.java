package com.accenture.insuranceclaimvalidation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.dto.response.FileUploadResponse;
import com.accenture.insuranceclaimvalidation.service.ClaimService;

@RestController
@RequestMapping("/api/v1/claims")
@Validated
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadClaim(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(claimService.uploadClaim(file));
    }
}