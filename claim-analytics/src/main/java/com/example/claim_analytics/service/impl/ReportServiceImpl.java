package com.example.claim_analytics.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;

import com.example.claim_analytics.dto.response.ReportDTO;
import com.example.claim_analytics.model.ClaimTatReportView;
import com.example.claim_analytics.repository.ReportRepository;
import com.example.claim_analytics.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
	private final ReportRepository reportRepository;

	@Override
	@Transactional(readOnly = true)
	public List<ReportDTO> getTatReport(OffsetDateTime from, OffsetDateTime to) { //, String country

		validatePeriod(from, to);

//		String normalizedCountry = normalizeCountry(country);
		
		LocalDateTime fromUtc = from
	            .withOffsetSameInstant(ZoneOffset.UTC)
	            .toLocalDateTime();

	    LocalDateTime toUtc = to
	            .withOffsetSameInstant(ZoneOffset.UTC)
	            .toLocalDateTime();

		List<ClaimTatReportView> results = reportRepository.getTatReport(fromUtc, toUtc);

		return results.stream().map(this::mapToDto).toList();
	}

	// ================================
	// PRIVATE METHODS
	// ================================

	private void validatePeriod(OffsetDateTime from, OffsetDateTime to) {

		if (from == null || to == null) {
			throw new IllegalArgumentException("From and To must not be null");
		}

		if (from.isAfter(to)) {
			throw new IllegalArgumentException("From must be before To");
		}

		if (from.equals(to)) {
			throw new IllegalArgumentException("From and To cannot be equal");
		}
	}

//	private String normalizeCountry(String country) {
//
//		if (!StringUtils.hasText(country)) {
//			return null;
//		}
//
//		return country.trim().toUpperCase();
//	}

	private ReportDTO mapToDto(ClaimTatReportView view) {

		return ReportDTO.builder().status(view.getStatus()).claimType(view.getClaimType()).totalClaims(view.getTotalClaims())
				.avgTatMinutes(view.getAvgTatMinutes()).minTatMinutes(view.getMinTatMinutes())
				.maxTatMinutes(view.getMaxTatMinutes()).build();
	}
}
