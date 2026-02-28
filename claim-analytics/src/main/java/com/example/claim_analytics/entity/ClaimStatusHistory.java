package com.example.claim_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

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

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "STATUS_AT", nullable = false)
    private OffsetDateTime statusAt;

    @Column(name = "IS_FINAL", nullable = false)
    private Integer isFinal;
}