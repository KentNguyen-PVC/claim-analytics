package com.example.claim_analytics.service;

import java.time.OffsetDateTime;
import java.util.List;

import com.example.claim_analytics.dto.response.ReportDTO;

public interface ReportService {

	List<ReportDTO> getTatReport(OffsetDateTime from, OffsetDateTime to);
}
