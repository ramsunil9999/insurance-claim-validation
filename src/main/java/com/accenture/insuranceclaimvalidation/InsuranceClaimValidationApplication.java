package com.accenture.insuranceclaimvalidation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class InsuranceClaimValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsuranceClaimValidationApplication.class, args);
    }
}