package com.example.claim_analytics.service;

import java.time.Instant;
import java.util.List;

import com.example.claim_analytics.dto.ReportDTO;

public interface ReportService {

	List<ReportDTO> getTatReport(Instant from, Instant to, String country);
}
