package com.example.claim_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

import com.example.claim_analytics.enums.ClaimStatus;

@Entity
@Table(name = "CLAIM_STATUS_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CLAIM_ID", nullable = false)
    private Long claimId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "FROM_STATUS", length = 20)
    private ClaimStatus fromStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TO_STATUS", nullable = false, length = 20)
    private ClaimStatus toStatus;

    @Column(name = "CHANGED_AT", nullable = false)
    private OffsetDateTime changedAt;

    @Column(name = "IS_FINAL")
    private Integer isFinal;
    
    @Column(name = "NOTE")
    private String note;
}