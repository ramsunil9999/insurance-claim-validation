package com.accenture.insuranceclaimvalidation.service.impl;

import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.accenture.insuranceclaimvalidation.service.OCRImageService;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

@Service
public class OCRImageServiceImpl implements OCRImageService {

    private final ITesseract tesseract;

    public OCRImageServiceImpl(@Value("${tesseract.data.path:}") String tesseractDataPath) {

        this.tesseract = new Tesseract();
        if (!tesseractDataPath.isBlank()) {
            tesseract.setDatapath(tesseractDataPath);
        }
        tesseract.setLanguage("eng");
    }

    @Override
    public String extractText(BufferedImage image) {

        try {
            return tesseract.doOCR(image);
        } catch (Exception e) {
            throw new RuntimeException("OCR processing failed.", e);
        }
    }
}