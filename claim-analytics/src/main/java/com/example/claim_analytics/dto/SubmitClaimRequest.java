package com.example.claim_analytics.dto;

import lombok.Builder;
import lombok.Data;


// For test
@Data
@Builder
public class SubmitClaimRequest {
    private String claimNo;
    private String claimType;
    private String policyNo;
    private String countryCode;
}