package com.example.claim_analytics.enums;

public enum ClaimStatus {
	SUBMITTED, APPROVED, REJECTED;

	public boolean canTransitionTo(ClaimStatus newStatus) {
		if (this == SUBMITTED) {
			return newStatus == APPROVED || newStatus == REJECTED;
		}
		return false;
	}
}