package com.accenture.insuranceclaimvalidation.service;

import com.accenture.insuranceclaimvalidation.enums.DocumentType;

public interface DocumentClassificationService {

    DocumentType classify(String extractedText);
    
}