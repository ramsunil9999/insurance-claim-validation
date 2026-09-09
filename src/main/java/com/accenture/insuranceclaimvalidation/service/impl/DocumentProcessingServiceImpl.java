package com.accenture.insuranceclaimvalidation.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.exception.InvalidFileException;
import com.accenture.insuranceclaimvalidation.service.DocumentProcessingService;
import com.accenture.insuranceclaimvalidation.service.extractor.DocumentExtractor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

    private final List<DocumentExtractor> documentExtractors;

    public DocumentProcessingServiceImpl(List<DocumentExtractor> documentExtractors) {
        this.documentExtractors = documentExtractors;
    }

    @Override
    public String processDocument(MultipartFile file) {

        return documentExtractors.stream()
                .filter(extractor -> extractor.supports(file))
                .findFirst()
                .orElseThrow(() -> new InvalidFileException("Unsupported file format"))
                .extractText(file);
    }
}