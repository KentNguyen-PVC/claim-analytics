package com.example.claim_analytics.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.claim_analytics.dto.request.CreateClaimRequest;
import com.example.claim_analytics.dto.request.UpdateClaimStatusRequest;
import com.example.claim_analytics.dto.response.ClaimResponse;
import com.example.claim_analytics.dto.response.PageResponse;
import com.example.claim_analytics.entity.Claim;
import com.example.claim_analytics.entity.Policy;
import com.example.claim_analytics.enums.ClaimStatus;
import com.example.claim_analytics.enums.PolicyStatus;
import com.example.claim_analytics.exception.BadRequestException;
import com.example.claim_analytics.exception.ConflictException;
import com.example.claim_analytics.exception.NotFoundException;
import com.example.claim_analytics.mapper.ClaimMapper;
import com.example.claim_analytics.repository.ClaimRepository;
import com.example.claim_analytics.repository.PolicyRepository;
import com.example.claim_analytics.service.ClaimService;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimServiceImpl implements ClaimService {
	private final ClaimRepository claimRepository;
	private final PolicyRepository policyRepository;
	private final EntityManager entityManager;

	@Override
	@Transactional
	public ClaimResponse createClaim(CreateClaimRequest request) {

		log.info("Creating claim for policyId={}", request.getPolicyId());

		Policy policy = policyRepository.findById(request.getPolicyId())
				.orElseThrow(() -> new NotFoundException("Policy not found"));

		if (policy.getStatus() != PolicyStatus.ACTIVE) {
			throw new ConflictException("Policy is not ACTIVE");
		}

		if (request.getClaimAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("Claim amount must be greater than 0");
		}

		Claim claim = Claim.builder()
				.policy(policy)
				.claimNumber(generateClaimNumber())
				.claimDate(LocalDate.now())
				.claimAmount(request.getClaimAmount())
				.claimStatus(ClaimStatus.SUBMITTED)
				.claimType(request.getClaimType())
				.description(request.getDescription())
				.finalDecisionAt(LocalDateTime.now(ZoneOffset.UTC))
				.build();

		claimRepository.save(claim);

		log.info("Claim created successfully id={}", claim.getClaimId());

		return ClaimMapper.toResponse(claim);
	}

	@Override
	public ClaimResponse getClaim(Long claimId) {

		Claim claim = claimRepository.findByIdWithPolicy(claimId)
				.orElseThrow(() -> new NotFoundException("Claim not found"));

		return ClaimMapper.toResponse(claim);
	}

	@Override
	public PageResponse<ClaimResponse> listClaims(Long policyId, ClaimStatus status, int limit, int offset) {

		limit = Math.min(limit <= 0 ? 20 : limit, 100);
		offset = Math.max(offset, 0);

		Pageable pageable = PageRequest.of(offset, limit);

		Page<Claim> resultPage = claimRepository.findByPolicy_IdAndClaimStatus(policyId, status, pageable);

		List<ClaimResponse> responses = resultPage.getContent().stream().map(ClaimMapper::toResponse).toList();

		return new PageResponse<>(responses, resultPage.getTotalElements(), limit, offset);
	}

	@Override
	@Transactional
	public ClaimResponse updateStatus(Long claimId, UpdateClaimStatusRequest request) {

		Claim claim = claimRepository.findByIdWithPolicy(claimId)
				.orElseThrow(() -> new NotFoundException("Claim not found"));
		
		ClaimStatus currentStatus = claim.getClaimStatus();
	    ClaimStatus newStatus = request.getNewStatus();

		if (currentStatus == ClaimStatus.APPROVED || currentStatus == ClaimStatus.REJECTED) {
			throw new ConflictException("Claim already in terminal state");
		} else if (!currentStatus.canTransitionTo(newStatus)) {
			throw new BadRequestException("Invalid status transition");
		} else if (newStatus == ClaimStatus.APPROVED) {
			validateApprovedAmount(request.getApprovedAmount(), claim.getClaimAmount());
			claim.setApprovedAmount(request.getApprovedAmount());
			entityManager.flush();
		}
		
		if (newStatus == ClaimStatus.APPROVED || newStatus == ClaimStatus.REJECTED) {
	        claimRepository.finalizeClaim(
	                claimId,
	                currentStatus.name(),
	                newStatus.name()
	        );
	        entityManager.clear();

	        return getClaim(claimId);
	    }

		log.info("Claim {} updated to {}", claimId, request.getNewStatus());

		return ClaimMapper.toResponse(claim);
	}

	private void validateApprovedAmount(BigDecimal approvedAmount, BigDecimal claimAmount) {
		if (approvedAmount == null || approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("Approved amount must be greater than 0");
		}

		if (approvedAmount.compareTo(claimAmount) > 0) {
			throw new BadRequestException("Approved amount cannot exceed claim amount");
		}
	}

	private String generateClaimNumber() {
		return "CLM-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

}
