package com.accenture.insuranceclaimvalidation.service.extractor;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.service.OCRImageService;

@Service
public class ImageDocumentExtractor implements DocumentExtractor {

    private final OCRImageService ocrImageService;

    public ImageDocumentExtractor(OCRImageService ocrImageService) {
        this.ocrImageService = ocrImageService;
    }

    @Override
    public boolean supports(MultipartFile file) {

        String contentType = file.getContentType();

        return contentType != null &&
                (contentType.equalsIgnoreCase("image/png")
                || contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/jpg"));
    }

    @Override
    public String extractText(MultipartFile file) {

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return ocrImageService.extractText(image);
        } catch (IOException e) {
            throw new RuntimeException("Unable to process image.", e);
        }
        
    }
}
