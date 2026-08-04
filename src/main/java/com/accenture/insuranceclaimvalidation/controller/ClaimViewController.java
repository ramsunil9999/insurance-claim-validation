package com.accenture.insuranceclaimvalidation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.accenture.insuranceclaimvalidation.dto.response.FileUploadResponse;
import com.accenture.insuranceclaimvalidation.exception.InvalidFileException;
import com.accenture.insuranceclaimvalidation.service.ClaimService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ClaimViewController {

    private final ClaimService claimService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClaimViewController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/")
    public String showUploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadClaim(@RequestParam("file") MultipartFile file, Model model) {
        try {
            FileUploadResponse response = claimService.uploadClaim(file);
            model.addAttribute("response", response);
            model.addAttribute("message", response.getMessage());

            try {
                String responseJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                model.addAttribute("responseJson", responseJson);
            } catch (JsonProcessingException e) {
                // if serialization fails, fall back to not showing JSON
            }

            return "upload";

        } catch (InvalidFileException ex) {
            model.addAttribute("error", ex.getMessage());
            return "upload";

        } catch (com.accenture.insuranceclaimvalidation.exception.AIException ex) {
            // AI extraction failed (invalid date / missing fields / parse error)
            model.addAttribute("error", "Failed to extract claim details from the document: " + ex.getMessage());
            return "upload";

        } catch (Exception ex) {
            // Generic fallback for unexpected errors
            model.addAttribute("error", "An unexpected error occurred while processing the file. Please check the document and try again.");
            return "upload";
        }
    }
}
