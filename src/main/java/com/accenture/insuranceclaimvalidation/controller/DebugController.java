package com.accenture.insuranceclaimvalidation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.insuranceclaimvalidation.dto.policy.PolicyRetrievalRequest;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController 
@RequestMapping ("/debug")
public class DebugController {

    @PostMapping ("/policy")
    public PolicyRetrievalRequest test(@RequestBody PolicyRetrievalRequest request) {

        System.out.println(
                "Received Query = "
                + request.getQuery());

        return request;
    }
}
