package com.accenture.insuranceclaimvalidation.service;

import org.springframework.web.multipart.MultipartFile;

public interface PDFTextExtractor {

    String extractText(MultipartFile file);
}