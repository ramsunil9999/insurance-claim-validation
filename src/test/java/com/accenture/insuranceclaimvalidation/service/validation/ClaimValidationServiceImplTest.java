package com.accenture.insuranceclaimvalidation.service.validation;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;
import com.accenture.insuranceclaimvalidation.service.validation.impl.ClaimValidationServiceImpl;

class ClaimValidationServiceImplTest {

    private final ClaimValidationService validationService = new ClaimValidationServiceImpl();

    @Test
        void doesNotFabricateClinicalAlignmentBeforeAiAssessment() {
        ClaimDetails claimDetails = ClaimDetails.builder()
                .diagnosis("Migraine")
                .procedurePerformed("Coronary Artery Bypass Grafting (CABG)")
                .doctorSpeciality("Cardiologist")
                .build();

        ValidationResult result = validationService.validate(claimDetails);

        assertNull(result.getProcedureDiagnosisValid());
        assertNull(result.getDoctorSpecialtyValid());
    }
}
