package com.example.claim_analytics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.claim_analytics.dto.request.CreateClaimRequest;
import com.example.claim_analytics.dto.request.UpdateClaimStatusRequest;
import com.example.claim_analytics.dto.response.ClaimResponse;
import com.example.claim_analytics.dto.response.PageResponse;
import com.example.claim_analytics.enums.ClaimStatus;
import com.example.claim_analytics.service.ClaimService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

	private final ClaimService claimService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ClaimResponse create(@Valid @RequestBody CreateClaimRequest request) {
		return claimService.createClaim(request);
	}

	@GetMapping("/{claimId}")
	public ClaimResponse getById(@PathVariable Long claimId) {
		return claimService.getClaim(claimId);
	}

	@GetMapping
	public PageResponse<ClaimResponse> list(@RequestParam(required = false) Long policyId,
			@RequestParam(required = false) ClaimStatus status, 
			@RequestParam(defaultValue = "20") int limit,
			@RequestParam(defaultValue = "0") int offset) {
		return claimService.listClaims(policyId, status, limit, offset);
	}

	@PatchMapping("/{claimId}")
	public ClaimResponse updateStatus(@PathVariable Long claimId, @Valid @RequestBody UpdateClaimStatusRequest request) {
		return claimService.updateStatus(claimId, request);
	}
}