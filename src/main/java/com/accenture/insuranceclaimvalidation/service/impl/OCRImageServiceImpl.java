package com.accenture.insuranceclaimvalidation.service.impl;

import java.awt.image.BufferedImage;

import org.springframework.stereotype.Service;

import com.accenture.insuranceclaimvalidation.service.OCRImageService;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

@Service
public class OCRImageServiceImpl implements OCRImageService {

    @Override
    public String extractText(BufferedImage image) {

        try {

            ITesseract tesseract = new Tesseract();

            tesseract.setDatapath(
                "C:\\Users\\vinay.kumar.jamalpur\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata"
            );

            tesseract.setLanguage("eng");

            return tesseract.doOCR(image);

        } catch (Exception e) {
            throw new RuntimeException("OCR processing failed.", e);
        }
    }  
}