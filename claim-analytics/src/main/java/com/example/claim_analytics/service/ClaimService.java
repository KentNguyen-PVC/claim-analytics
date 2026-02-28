package com.example.claim_analytics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.claim_analytics.dto.SubmitClaimRequest;
import com.example.claim_analytics.repository.ClaimRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimService {

    private final ClaimRepository claimRepository;

    public Long submitClaim(SubmitClaimRequest request) {

        return claimRepository.createClaim(
                request.getClaimNo(),
                request.getClaimType(),
                request.getPolicyNo(),
                request.getCountryCode()
        );
    }

    public void approveClaim(Long claimId) {
        claimRepository.finalizeClaim(
                claimId,
                "APPROVED"
        );
    }

    public void rejectClaim(Long claimId) {
        claimRepository.finalizeClaim(
                claimId,
                "REJECTED"
        );
    }
}