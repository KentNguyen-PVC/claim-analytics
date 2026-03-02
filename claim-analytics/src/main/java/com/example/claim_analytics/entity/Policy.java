package com.example.claim_analytics.entity;

import java.time.OffsetDateTime;

import com.example.claim_analytics.enums.PolicyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "POLICY", indexes = { @Index(name = "idx_policy_status", columnList = "status") })
public class Policy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "policy_number", nullable = false, unique = true, length = 50)
	private String policyNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private PolicyStatus status;
	
    @Column(name = "COUNTRY_CODE", nullable = false)
    private String countryCode;

	@Column(name = "effective_date", nullable = false)
	private OffsetDateTime effectiveDate;

	@Column(name = "expiry_date", nullable = false)
	private OffsetDateTime expiryDate;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = OffsetDateTime.now();
	}
}