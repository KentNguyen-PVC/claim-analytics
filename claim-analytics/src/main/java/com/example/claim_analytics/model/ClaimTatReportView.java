package com.example.claim_analytics.model;

public interface ClaimTatReportView {

	String getStatus();
	
	String getClaimType();

	Long getTotalClaims();

	Double getAvgTatMinutes();

	Long getMinTatMinutes();

	Long getMaxTatMinutes();
}