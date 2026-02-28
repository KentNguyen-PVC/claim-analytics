package com.example.claim_analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDTO {

	private String claimType;
	private String status;
	private Long totalClaims;
	private Double avgTatMinutes;
	private Long minTatMinutes;
	private Long maxTatMinutes;
}