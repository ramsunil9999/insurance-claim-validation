package com.accenture.insuranceclaimvalidation.dto.policy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRetrievalResponse {

    private String query;

    private String policy_context;
}