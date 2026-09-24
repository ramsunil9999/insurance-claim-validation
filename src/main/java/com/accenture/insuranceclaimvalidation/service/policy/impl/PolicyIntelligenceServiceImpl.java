package com.accenture.insuranceclaimvalidation.service.policy.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.accenture.insuranceclaimvalidation.dto.policy.PolicyRetrievalRequest;
import com.accenture.insuranceclaimvalidation.dto.policy.PolicyRetrievalResponse;
import com.accenture.insuranceclaimvalidation.service.policy.PolicyIntelligenceService;

@Service
public class PolicyIntelligenceServiceImpl implements PolicyIntelligenceService {

    private final RestTemplate restTemplate;

    public PolicyIntelligenceServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String retrievePolicyContext(String query) {

        PolicyRetrievalRequest request = PolicyRetrievalRequest.builder().query(query).build();

        System.out.println("Sending Request = " + request);

        PolicyRetrievalResponse response = restTemplate.postForObject(
                "http://localhost:8000/retrieve",
                request,
                PolicyRetrievalResponse.class);

        if (response == null) {
            return "";
        }

        return response.getPolicy_context();
    }
}