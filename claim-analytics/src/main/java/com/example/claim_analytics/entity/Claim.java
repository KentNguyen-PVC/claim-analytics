package com.example.claim_analytics.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CLAIM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CLAIM_NO", nullable = false, unique = true)
    private String claimNo;

    @Column(name = "CLAIM_TYPE", nullable = false)
    private String claimType;
    
    @Column(name = "FINAL_STATUS", nullable = false)
    private String finalStatus;

    @Column(name = "COUNTRY_CODE", nullable = false)
    private String countryCode;

    @Column(name = "POLICY_NO", nullable = false)
    private String policyNo;

    @Column(name = "SUBMITTED_AT")
    private Instant submittedAt;

    @Column(name = "FINAL_DECISION_AT")
    private Instant finalDecisionAt;

    @Column(name = "TAT_WORKING_MINUTES")
    private Long tatWorkingMinutes;

    @Column(name = "TAT_CALENDAR_MINUTES")
    private Long tatCalendarMinutes;

    @Column(name = "TAT_VERSION")
    private Integer tatVersion;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;
}