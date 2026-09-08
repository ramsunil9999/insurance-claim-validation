package com.accenture.insuranceclaimvalidation.service.validation;

import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationDetails;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;

public interface PriorAuthorizationValidationService {

    ValidationResult validate(PriorAuthorizationDetails priorAuthorizationDetails);
}