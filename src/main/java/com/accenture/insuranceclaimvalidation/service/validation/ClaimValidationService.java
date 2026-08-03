package com.accenture.insuranceclaimvalidation.service.validation;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;

public interface ClaimValidationService {

    ValidationResult validate(ClaimDetails claimDetails);

}