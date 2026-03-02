package com.example.claim_analytics.dto.request;

import java.math.BigDecimal;

import com.example.claim_analytics.enums.ClaimType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClaimRequest {

	@NotNull
	private Long policyId;

	@NotNull
	@DecimalMin("0.01")
	private BigDecimal claimAmount;

	@NotNull
	private ClaimType claimType;

	@Size(max = 500)
	private String description;
}