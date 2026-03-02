package com.example.claim_analytics.service;

import com.example.claim_analytics.dto.request.CreateClaimRequest;
import com.example.claim_analytics.dto.request.UpdateClaimStatusRequest;
import com.example.claim_analytics.dto.response.ClaimResponse;
import com.example.claim_analytics.dto.response.PageResponse;
import com.example.claim_analytics.enums.ClaimStatus;

public interface ClaimService {
	ClaimResponse createClaim(CreateClaimRequest request);

	ClaimResponse getClaim(Long id);

	PageResponse<ClaimResponse> listClaims(Long policyId, ClaimStatus status, int limit, int offset);

	ClaimResponse updateStatus(Long id, UpdateClaimStatusRequest request);

}