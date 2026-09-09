package com.accenture.insuranceclaimvalidation.service.impl;

import org.springframework.stereotype.Service;

import com.accenture.insuranceclaimvalidation.enums.DocumentType;
import com.accenture.insuranceclaimvalidation.service.DocumentClassificationService;

@Service
public class DocumentClassificationServiceImpl implements DocumentClassificationService {

    @Override
    public DocumentType classify(String extractedText) {

        String text = extractedText == null ? "" : extractedText.toLowerCase();

        if (containsPriorAuthorizationIndicators(text)) {
            return DocumentType.PRIOR_AUTHORIZATION;
        }

        return DocumentType.CLAIM;
    }

    private boolean containsPriorAuthorizationIndicators(String text) {

        return text.contains("prior authorization")
                || text.contains("prior authorisation")
                || text.contains("requested procedure")
                || text.contains("medical necessity")
                || text.contains("procedure category")
                || text.contains("estimated cost")
                || text.contains("treatment plan")
                || text.contains("diagnosis code")
                || text.contains("procedure code");
    }
}