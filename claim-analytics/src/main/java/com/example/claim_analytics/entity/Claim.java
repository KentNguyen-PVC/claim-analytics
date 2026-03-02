package com.example.claim_analytics.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.example.claim_analytics.enums.ClaimStatus;
import com.example.claim_analytics.enums.ClaimType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
	private Long claimId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "policy_id", nullable = false)
	private Policy policy;

	@Column(nullable = false, unique = true)
	private String claimNumber;

	@Column(nullable = false)
	private LocalDate claimDate;

	@Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal claimAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClaimStatus claimStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClaimType claimType;

	@Column(length = 500)
	private String description;
	
    @Column(name = "FINAL_DECISION_AT")
    private LocalDateTime finalDecisionAt;
    
    @Column(name = "TAT_WORKING_MINUTES")
    private Long tatWorkingMinutes;

	@Column(nullable = false, updatable = false)
	private OffsetDateTime createdAt;
	
	@Column(nullable = false)
	private OffsetDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
	    OffsetDateTime now = OffsetDateTime.now();
	    this.createdAt = now;
	    this.updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
	    this.updatedAt = OffsetDateTime.now();
	}
}