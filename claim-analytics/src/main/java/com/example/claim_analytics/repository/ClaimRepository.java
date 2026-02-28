package com.example.claim_analytics.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClaimRepository {

	private final JdbcTemplate jdbcTemplate;

	public Long createClaim(String claimNo, String claimType, String policyNo, String countryCode) {

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement("""
					    INSERT INTO CLAIM (
					        CLAIM_NO,
					        CLAIM_TYPE,
					        POLICY_NO,
					        COUNTRY_CODE,
					        FINAL_STATUS,
					        SUBMITTED_AT,
					        FINAL_DECISION_AT
					    )
					    VALUES (?, ?, ?, ?, 'SUBMITTED', SYSTIMESTAMP, SYSTIMESTAMP)
					""", new String[]{"ID"});

			ps.setString(1, claimNo);
			ps.setString(2, claimType);
			ps.setString(3, policyNo);
			ps.setString(4, countryCode);

			return ps;
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	public void finalizeClaim(Long claimId, String status) {

		jdbcTemplate.update("BEGIN CLAIM_SLA_PKG.finalize_claim(?,?); END;", claimId, status);
	}
}