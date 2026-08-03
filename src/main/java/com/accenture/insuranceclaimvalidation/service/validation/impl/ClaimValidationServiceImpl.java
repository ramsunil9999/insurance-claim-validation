package com.accenture.insuranceclaimvalidation.service.validation.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.accenture.insuranceclaimvalidation.dto.ClaimDetails;
import com.accenture.insuranceclaimvalidation.dto.ValidationResult;
import com.accenture.insuranceclaimvalidation.service.validation.ClaimValidationService;

@Service
public class ClaimValidationServiceImpl implements ClaimValidationService {

    @Override
    public ValidationResult validate(ClaimDetails claimDetails) {

        List<String> errors = new ArrayList<>();

        validatePatientName(claimDetails, errors);
        validateAge(claimDetails, errors);
        validateGender(claimDetails, errors);

        validatePolicyNumber(claimDetails, errors);
        validateMemberId(claimDetails, errors);
        validateInsurancePlan(claimDetails, errors);

        validateHospitalName(claimDetails, errors);
        validateHospitalType(claimDetails, errors);
        validateHospitalCity(claimDetails, errors);

        validateDoctorName(claimDetails, errors);
        validateDoctorSpeciality(claimDetails, errors);

        validateDiagnosis(claimDetails, errors);
        validateTreatment(claimDetails, errors);

        validateAdmissionDate(claimDetails, errors);
        validateDischargeDate(claimDetails, errors);
        validateDateSequence(claimDetails, errors);
        validateLengthOfStay(claimDetails, errors);

        validateClaimAmount(claimDetails, errors);
        validateRoomCharges(claimDetails, errors);
        validateMedicineCharges(claimDetails, errors);
        validateLabCharges(claimDetails, errors);
        validateProcedureCharges(claimDetails, errors);
        validateDoctorConsultationCharges(claimDetails, errors);

        validateFinancialConsistency(claimDetails, errors);

        return ValidationResult.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    private void validatePatientName(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getPatientName())) {
            errors.add("Patient Name is mandatory.");
        }
    }

    private void validateAge(ClaimDetails claimDetails, List<String> errors) {
        if (claimDetails.getAge() == null) {
            errors.add("Patient Age is mandatory.");
            return;
        }

        if (claimDetails.getAge() < 0 || claimDetails.getAge() > 120) {
            errors.add("Patient Age is invalid.");
        }
    }

    private void validateGender(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getGender())) {
            errors.add("Gender is mandatory.");
        }
    }

    private void validatePolicyNumber(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getPolicyNumber())) {
            errors.add("Policy Number is mandatory.");
        }
    }

    private void validateMemberId(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getMemberId())) {
            errors.add("Member ID is mandatory.");
        }
    }

    private void validateInsurancePlan(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getInsurancePlan())) {
            errors.add("Insurance Plan is mandatory.");
        }
    }

    private void validateHospitalName(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getHospitalName())) {
            errors.add("Hospital Name is mandatory.");
        }
    }

    private void validateHospitalType(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getHospitalType())) {
            errors.add("Hospital Type is mandatory.");
        }
    }

    private void validateHospitalCity(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getHospitalCity())) {
            errors.add("Hospital City is mandatory.");
        }
    }

    private void validateDoctorName(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getDoctorName())) {
            errors.add("Doctor Name is mandatory.");
        }
    }

    private void validateDoctorSpeciality(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getDoctorSpeciality())) {
            errors.add("Doctor Speciality is mandatory.");
        }
    }

    private void validateDiagnosis(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getDiagnosis())) {
            errors.add("Diagnosis is mandatory.");
        }
    }

    private void validateTreatment(ClaimDetails claimDetails, List<String> errors) {
        if (isBlank(claimDetails.getTreatmentProvided())) {
            errors.add("Treatment information is mandatory.");
        }
    }

    private void validateAdmissionDate(ClaimDetails claimDetails, List<String> errors) {
        if (claimDetails.getAdmissionDate() == null) {
            errors.add("Admission Date is mandatory.");
        }
    }

    private void validateDischargeDate(ClaimDetails claimDetails, List<String> errors) {
        if (claimDetails.getDischargeDate() == null) {
            errors.add("Discharge Date is mandatory.");
        }
    }

    private void validateDateSequence(ClaimDetails claimDetails, List<String> errors) {
        LocalDate admission = claimDetails.getAdmissionDate();
        LocalDate discharge = claimDetails.getDischargeDate();
        if (admission == null || discharge == null) {
            return;
        }

        if (discharge.isBefore(admission)) {
            errors.add("Discharge Date cannot be before Admission Date.");
        }
    }

    private void validateLengthOfStay(ClaimDetails claimDetails, List<String> errors) {
        LocalDate admission = claimDetails.getAdmissionDate();
        LocalDate discharge = claimDetails.getDischargeDate();

        if (admission == null || discharge == null) {
            return;
        }

        long actualStay = ChronoUnit.DAYS.between(admission, discharge);

        if (actualStay < 0) {
            return;
        }

        if (claimDetails.getLengthOfStay() == null) {
            return;
        }

        if (!claimDetails.getLengthOfStay().equals((int) actualStay)) {
            errors.add("Length of Stay does not match Admission and Discharge Dates.");
        }
    }

    private void validateClaimAmount(ClaimDetails claimDetails, List<String> errors) {
        if (claimDetails.getClaimAmount() == null) {
            errors.add("Claim Amount is mandatory.");
            return;
        }
        if (claimDetails.getClaimAmount() <= 0) {
            errors.add("Claim Amount must be greater than zero.");
        }
    }

    private void validateRoomCharges(ClaimDetails claimDetails, List<String> errors) {
        validatePositiveAmount(claimDetails.getRoomCharges(), "Room Charges", errors);
    }

    private void validateMedicineCharges(ClaimDetails claimDetails, List<String> errors) {
        validatePositiveAmount(claimDetails.getMedicineCharges(), "Medicine Charges", errors);
    }

    private void validateLabCharges(ClaimDetails claimDetails, List<String> errors) {
        validatePositiveAmount(claimDetails.getLabCharges(), "Lab Charges", errors);
    }

    private void validateProcedureCharges(ClaimDetails claimDetails, List<String> errors) {
        validatePositiveAmount(claimDetails.getProcedureCharges(), "Procedure Charges", errors);
    }

    private void validateDoctorConsultationCharges(ClaimDetails claimDetails, List<String> errors) {
        validatePositiveAmount(claimDetails.getDoctorConsultationCharges(), "Doctor Consultation Charges", errors);
    }

    private void validateFinancialConsistency(ClaimDetails claimDetails, List<String> errors) {
        if (claimDetails.getClaimAmount() == null) {
            return;
        }
        double total = 0;
        total += value(claimDetails.getRoomCharges());
        total += value(claimDetails.getMedicineCharges());
        total += value(claimDetails.getLabCharges());
        total += value(claimDetails.getProcedureCharges());
        total += value(claimDetails.getDoctorConsultationCharges());

        if (total > 0) {
            double difference = Math.abs(claimDetails.getClaimAmount() - total);
            if (difference > 5.0) {
                errors.add("Claim Amount does not match the total of individual charges.");
            }
        }
    }

    private void validatePositiveAmount(Double amount, String fieldName, List<String> errors) {
        if (amount == null) {
            return;
        }
        if (amount < 0) {
            errors.add(fieldName + " cannot be negative.");
        }
    }

    private double value(Double amount) {
        return amount == null ? 0.0 : amount;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}