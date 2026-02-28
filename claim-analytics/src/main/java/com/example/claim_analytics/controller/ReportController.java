package com.example.claim_analytics.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.claim_analytics.dto.ReportDTO;
import com.example.claim_analytics.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
	
	private final ReportService reportService;

	@GetMapping("/tat")
	public ResponseEntity<List<ReportDTO>> getTatReport(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
			@RequestParam(required = false) String country) {

//		OffsetDateTime fromInstant = from.toInstant();
//		Instant toInstant = to.toInstant();

		List<ReportDTO> response = reportService.getTatReport(from, to, country);

		return ResponseEntity.ok(response);
	}
}
