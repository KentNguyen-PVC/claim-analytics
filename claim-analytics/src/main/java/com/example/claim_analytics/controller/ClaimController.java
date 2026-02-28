package com.example.claim_analytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.claim_analytics.dto.SubmitClaimRequest;
import com.example.claim_analytics.service.ClaimService;

import lombok.RequiredArgsConstructor;

// For test
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

	private final ClaimService claimService;

	@PostMapping("/submit")
	public ResponseEntity<Long> submit(@RequestBody SubmitClaimRequest request) {

		Long id = claimService.submitClaim(request);
		return ResponseEntity.ok(id);
	}

	@PostMapping("/{id}/approve")
	public ResponseEntity<Void> approve(@PathVariable Long id) {

		claimService.approveClaim(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<Void> reject(@PathVariable Long id) {

		claimService.rejectClaim(id);
		return ResponseEntity.ok().build();
	}
}