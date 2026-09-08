package com.accenture.insuranceclaimvalidation.service.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.accenture.insuranceclaimvalidation.dto.PriorAuthorizationDetails;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;
import com.accenture.insuranceclaimvalidation.service.validation.PriorAuthorizationValidationService;

@Service
public class PriorAuthorizationValidationServiceImpl implements PriorAuthorizationValidationService {

    @Override
    public ValidationResult validate(PriorAuthorizationDetails details) {

        List<String> errors = new ArrayList<>();

        validatePatientName(details, errors);
        validateAge(details, errors);
        validateGender(details, errors);

        validatePolicyNumber(details, errors);
        validateMemberId(details, errors);
        validateInsurancePlan(details, errors);

        validateHospitalName(details, errors);
        validateHospitalType(details, errors);
        validateHospitalCity(details, errors);

        validateDoctorName(details, errors);
        validateDoctorSpeciality(details, errors);

        validatePrimaryDiagnosis(details, errors);

        validateRequestedProcedure(details, errors);
        validateProcedureCategory(details, errors);

        validateMedicalNecessity(details, errors);

        validateRequestedProcedureDate(details, errors);

        validateEstimatedCost(details, errors);

        return ValidationResult.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    private void validatePatientName(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getPatientName())) {
            errors.add("Patient Name is mandatory.");
        }
    }

    private void validateAge(PriorAuthorizationDetails details, List<String> errors) {
        if (details.getAge() == null) {
            errors.add("Patient Age is mandatory.");
            return;
        }
        if (details.getAge() < 0 || details.getAge() > 120) {
            errors.add("Patient Age is invalid.");
        }
    }

    private void validateGender(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getGender())) {
            errors.add("Gender is mandatory.");
        }
    }

    private void validatePolicyNumber(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getPolicyNumber())) {
            errors.add("Policy Number is mandatory.");
        }
    }

    private void validateMemberId(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getMemberId())) {
            errors.add("Member ID is mandatory.");
        }
    }

    private void validateInsurancePlan(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getInsurancePlan())) {
            errors.add("Insurance Plan is mandatory.");
        }
    }

    private void validateHospitalName(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getHospitalName())) {
            errors.add("Hospital Name is mandatory.");
        }
    }

    private void validateHospitalType(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getHospitalType())) {
            errors.add("Hospital Type is mandatory.");
        }
    }

    private void validateHospitalCity(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getHospitalCity())) {
            errors.add("Hospital City is mandatory.");
        }
    }

    private void validateDoctorName(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getDoctorName())) {
            errors.add("Doctor Name is mandatory.");
        }
    }

    private void validateDoctorSpeciality(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getDoctorSpeciality())) {
            errors.add("Doctor Speciality is mandatory.");
        }
    }

    private void validatePrimaryDiagnosis(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getPrimaryDiagnosis())) {
            errors.add("Primary Diagnosis is mandatory.");
        }
    }

    private void validateRequestedProcedure(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getRequestedProcedure())) {
            errors.add("Requested Procedure is mandatory.");
        }
    }

    private void validateProcedureCategory(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getProcedureCategory())) {
            errors.add("Procedure Category is mandatory.");
        }
    }

    private void validateMedicalNecessity(PriorAuthorizationDetails details, List<String> errors) {
        if (isBlank(details.getMedicalNecessityReason())) {
            errors.add("Medical Necessity Reason is mandatory.");
        }
    }

    private void validateRequestedProcedureDate(PriorAuthorizationDetails details, List<String> errors) {
        if (details.getRequestedProcedureDate() == null) {
            errors.add("Requested Procedure Date is mandatory.");
        }
    }

    private void validateEstimatedCost(PriorAuthorizationDetails details, List<String> errors) {
        if (details.getEstimatedCost() == null) {
            errors.add("Estimated Cost is mandatory.");
            return;
        }

        if (details.getEstimatedCost() <= 0) {
            errors.add("Estimated Cost must be greater than zero.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
