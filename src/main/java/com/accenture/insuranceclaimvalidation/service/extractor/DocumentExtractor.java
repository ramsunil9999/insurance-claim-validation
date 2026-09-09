package com.accenture.insuranceclaimvalidation.service.extractor;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentExtractor {

    boolean supports(MultipartFile file);

    String extractText(MultipartFile file);
}