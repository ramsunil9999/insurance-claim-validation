package com.accenture.insuranceclaimvalidation.service.extractor;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.exception.InvalidFileException;
import com.accenture.insuranceclaimvalidation.service.OCRImageService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfDocumentExtractor implements DocumentExtractor {

    private final OCRImageService ocrImageService;

    public PdfDocumentExtractor(OCRImageService ocrImageService) {
        this.ocrImageService = ocrImageService;
    }

    @Override
    public boolean supports(MultipartFile file) {
        return file.getContentType() != null
                && file.getContentType().equalsIgnoreCase("application/pdf");
    }

    @Override
    public String extractText(MultipartFile file) {

        String extractedText = extractPdfText(file);

        if (extractedText != null && !extractedText.isBlank()) {
            log.info("Digital PDF is uploaded and text is extracted successfully.");
            return extractedText;
        }

        log.info("Scanned PDF is uploaded. Performing OCR on all pages.");

        return performOCRForAllPages(file);
    }

    private String performOCRForAllPages(MultipartFile file) {

        StringBuilder extractedText = new StringBuilder();

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDFRenderer renderer = new PDFRenderer(document);

            int totalPages = document.getNumberOfPages();
            log.info("Performing OCR on {} pages.", totalPages);

            for (int page = 0; page < totalPages; page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 300);
                String pageText = ocrImageService.extractText(image);
                extractedText.append(pageText).append(System.lineSeparator()).append(System.lineSeparator());
            }
            return extractedText.toString();

        } catch (IOException e) {
            log.error("Unable to render PDF pages");
            throw new RuntimeException("Unable to render PDF pages.", e);
        }

    }

    public String extractPdfText(MultipartFile file) {

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);

        } catch (IOException e) {
            throw new InvalidFileException("Unable to read the uploaded PDF.");
        }
    }
}
