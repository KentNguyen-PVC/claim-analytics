package com.example.claim_analytics.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
	private OffsetDateTime timestamp;
	private int status;
	private String error;
}