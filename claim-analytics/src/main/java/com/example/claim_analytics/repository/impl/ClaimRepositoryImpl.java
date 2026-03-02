package com.example.claim_analytics.repository.impl;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.example.claim_analytics.repository.ClaimRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClaimRepositoryImpl implements ClaimRepositoryCustom {

	private final JdbcTemplate jdbcTemplate;

	@Override
	public void finalizeClaim(Long claimId, String fromStatus, String toStatus) {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withCatalogName("CLAIM_SLA_PKG")
				.withProcedureName("finalize_claim");

		jdbcCall.execute(Map.of("p_claim_id", claimId, "p_from_status", fromStatus, "p_status", toStatus));
	}
}