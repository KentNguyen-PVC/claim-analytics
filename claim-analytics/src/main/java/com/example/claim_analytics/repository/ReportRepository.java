package com.example.claim_analytics.repository;

import java.time.LocalDateTime;
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
            c.claimStatus as status,
            COUNT(c.id) as totalClaims,
            AVG(c.tatWorkingMinutes) as avgTatMinutes,
            MIN(c.tatWorkingMinutes) as minTatMinutes,
            MAX(c.tatWorkingMinutes) as maxTatMinutes
        FROM Claim c
        WHERE c.finalDecisionAt BETWEEN :from AND :to
          AND c.tatWorkingMinutes IS NOT NULL
        GROUP BY c.claimType, c.claimStatus
    """)
    // AND (:country IS NULL OR c.countryCode = :country)
    List<ClaimTatReportView> getTatReport(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
//            @Param("country") String country
    );
}