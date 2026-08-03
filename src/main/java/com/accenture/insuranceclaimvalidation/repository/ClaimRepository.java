package com.accenture.insuranceclaimvalidation.repository;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.accenture.insuranceclaimvalidation.entity.Claim;

@Repository
public interface ClaimRepository extends MongoRepository<Claim, String> {

    boolean existsByPolicyNumberAndMemberIdAndDiagnosisAndAdmissionDate(
        String policyNumber,
        String memberId,
        String diagnosis,
        LocalDate admissionDate);

}