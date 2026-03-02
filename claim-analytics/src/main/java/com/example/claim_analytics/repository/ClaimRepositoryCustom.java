package com.example.claim_analytics.repository;

public interface ClaimRepositoryCustom {
	
	void finalizeClaim(Long claimId, String fromStatus, String toStatus);
}