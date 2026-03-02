package com.example.claim_analytics.mapper;

import com.example.claim_analytics.dto.response.ClaimResponse;
import com.example.claim_analytics.entity.Claim;

public class ClaimMapper {

    private ClaimMapper() {}

    public static ClaimResponse toResponse(Claim claim) {
        return ClaimResponse.builder()
                .claimId(claim.getClaimId())
                .policyId(claim.getPolicy().getId())
                .claimNumber(claim.getClaimNumber())
                .claimDate(claim.getClaimDate())
                .claimAmount(claim.getClaimAmount())
                .approvedAmount(claim.getApprovedAmount())
                .claimStatus(claim.getClaimStatus())
                .claimType(claim.getClaimType())
                .description(claim.getDescription())
                .createdAt(claim.getCreatedAt())
                .build();
    }
}