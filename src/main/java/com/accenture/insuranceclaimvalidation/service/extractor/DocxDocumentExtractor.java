package com.accenture.insuranceclaimvalidation.service.extractor;

import java.io.IOException;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.exception.InvalidFileException;

@Service
public class DocxDocumentExtractor implements DocumentExtractor {

    @Override
    public boolean supports(MultipartFile file) {

        return file.getContentType() != null
                && file.getContentType().equalsIgnoreCase(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    @Override
    public String extractText(MultipartFile file) {

        try (XWPFDocument document =
                     new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor =
                     new XWPFWordExtractor(document)) {

            return extractor.getText();

        } catch (IOException e) {

            throw new InvalidFileException(
                    "Unable to read uploaded DOCX file.");
        }
    }
}