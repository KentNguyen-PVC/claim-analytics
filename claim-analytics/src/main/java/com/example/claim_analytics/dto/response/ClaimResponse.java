package com.example.claim_analytics.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.example.claim_analytics.enums.ClaimStatus;
import com.example.claim_analytics.enums.ClaimType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClaimResponse {

	private Long claimId;
	private Long policyId;
	private String claimNumber;
	private BigDecimal claimAmount;
	private BigDecimal approvedAmount;
	private ClaimStatus claimStatus;
	private ClaimType claimType;
	private LocalDate claimDate;
	private String description;
	private OffsetDateTime createdAt;
}