package com.accenture.insuranceclaimvalidation.dto.policy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRetrievalRequest {

    private String query;

}