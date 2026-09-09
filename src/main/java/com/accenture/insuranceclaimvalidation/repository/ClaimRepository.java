package com.accenture.insuranceclaimvalidation.repository;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.accenture.insuranceclaimvalidation.entity.Claim;
import com.accenture.insuranceclaimvalidation.enums.RequestType;

@Repository
public interface ClaimRepository extends MongoRepository<Claim, String> {

    boolean existsByPolicyNumberAndMemberIdAndDiagnosisAndAdmissionDate(
        String policyNumber,
        String memberId,
        String diagnosis,
        LocalDate admissionDate);

    java.util.Optional<Claim> findFirstByRequestTypeAndPriorAuthorizationId(
        RequestType requestType, String priorAuthorizationId);

}