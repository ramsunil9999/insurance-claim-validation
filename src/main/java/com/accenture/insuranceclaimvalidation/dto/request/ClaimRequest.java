package com.accenture.insuranceclaimvalidation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Claim amount should be greater than zero")
    private Double claimAmount;
}