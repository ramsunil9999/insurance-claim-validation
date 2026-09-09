package com.accenture.insuranceclaimvalidation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.accenture.insuranceclaimvalidation.entity.PriorAuthorization;

@Repository
public interface PriorAuthorizationRepository extends MongoRepository<PriorAuthorization, String> {

    
}