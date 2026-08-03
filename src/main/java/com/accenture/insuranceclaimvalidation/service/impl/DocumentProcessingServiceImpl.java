package com.accenture.insuranceclaimvalidation.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.service.DocumentProcessingService;
import com.accenture.insuranceclaimvalidation.service.OCRImageService;
import com.accenture.insuranceclaimvalidation.service.PDFTextExtractor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentProcessingServiceImpl implements DocumentProcessingService {

    private final PDFTextExtractor pdfTextExtractor;
    private final OCRImageService ocrImageService;

    public DocumentProcessingServiceImpl(
            PDFTextExtractor pdfTextExtractor,
            OCRImageService ocrImageService) {

        this.pdfTextExtractor = pdfTextExtractor;
        this.ocrImageService = ocrImageService;
    }

    @Override
    public String processDocument(MultipartFile file) {

        String extractedText = pdfTextExtractor.extractText(file);
        if (extractedText != null && !extractedText.isBlank()) {
            log.info("Digital PDF is uploaded and text is extracted successfully.");
            return extractedText;
        }
        BufferedImage image = renderFirstPage(file);

        log.info("Scanned PDF is uploaded. Performing OCR.");
        return ocrImageService.extractText(image);

    }

    private BufferedImage renderFirstPage(MultipartFile file) {

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDFRenderer renderer = new PDFRenderer(document);

            return renderer.renderImageWithDPI(0, 300);

        } catch (IOException e) {

            log.error("Unable to render the image from PDF");
            throw new RuntimeException("Unable to render PDF page.", e);

        }
    }
}
