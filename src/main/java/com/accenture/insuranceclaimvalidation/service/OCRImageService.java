package com.accenture.insuranceclaimvalidation.service;

import java.awt.image.BufferedImage;

public interface OCRImageService {

    String extractText(BufferedImage image);

}