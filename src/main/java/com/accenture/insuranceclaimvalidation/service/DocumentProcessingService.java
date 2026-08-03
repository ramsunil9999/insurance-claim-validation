package com.accenture.insuranceclaimvalidation.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentProcessingService {
    String processDocument(MultipartFile file);
}
