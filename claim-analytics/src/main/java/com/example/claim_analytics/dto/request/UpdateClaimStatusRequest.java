package com.example.claim_analytics.dto.request;

import java.math.BigDecimal;

import com.example.claim_analytics.enums.ClaimStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClaimStatusRequest {

	@NotNull
	private ClaimStatus newStatus;

	private BigDecimal approvedAmount;

	@Size(max = 500)
	private String note;
}