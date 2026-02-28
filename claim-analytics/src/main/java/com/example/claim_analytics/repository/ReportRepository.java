package com.example.claim_analytics.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.claim_analytics.entity.Claim;
import com.example.claim_analytics.model.ClaimTatReportView;

@Repository
public interface ReportRepository extends JpaRepository<Claim, Long> {

    @Query("""
        SELECT 
            c.claimType as claimType,
            c.finalStatus as status,
            COUNT(c.id) as totalClaims,
            AVG(c.tatWorkingMinutes) as avgTatMinutes,
            MIN(c.tatWorkingMinutes) as minTatMinutes,
            MAX(c.tatWorkingMinutes) as maxTatMinutes
        FROM Claim c
        WHERE c.finalDecisionAt BETWEEN :from AND :to
          AND c.tatWorkingMinutes IS NOT NULL
          AND (:country IS NULL OR c.countryCode = :country)
        GROUP BY c.claimType, c.finalStatus
    """)
    List<ClaimTatReportView> getTatReport(
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("country") String country
    );
}