package com.example.claim_analytics.service;

import com.example.claim_analytics.dto.request.CreateClaimRequest;
import com.example.claim_analytics.dto.request.UpdateClaimStatusRequest;
import com.example.claim_analytics.dto.response.ClaimResponse;
import com.example.claim_analytics.entity.Claim;
import com.example.claim_analytics.entity.Policy;
import com.example.claim_analytics.enums.ClaimStatus;
import com.example.claim_analytics.enums.ClaimType;
import com.example.claim_analytics.enums.PolicyStatus;
import com.example.claim_analytics.exception.BadRequestException;
import com.example.claim_analytics.exception.ConflictException;
import com.example.claim_analytics.exception.NotFoundException;
import com.example.claim_analytics.repository.ClaimRepository;
import com.example.claim_analytics.repository.PolicyRepository;
import com.example.claim_analytics.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ClaimServiceTest {

	@Mock
	private ClaimRepository claimRepository;

	@Mock
	private PolicyRepository policyRepository;

	@InjectMocks
	private ClaimServiceImpl claimService;

	private Policy activePolicy;

	@BeforeEach
	void setup() {
		activePolicy = new Policy();
		activePolicy.setId(1L);
		activePolicy.setStatus(PolicyStatus.ACTIVE);
	}

	private CreateClaimRequest buildValidRequest() {
		CreateClaimRequest request = new CreateClaimRequest();
		request.setPolicyId(1L);
		request.setClaimAmount(BigDecimal.valueOf(5000000));
		request.setClaimType(ClaimType.HOSPITALIZATION);
		request.setDescription("Emergency surgery");
		return request;
	}
	
	private Claim buildSubmittedClaim() {
	    Claim claim = new Claim();
	    claim.setClaimId(1L);
		claim.setPolicy(activePolicy);
	    claim.setClaimStatus(ClaimStatus.SUBMITTED);
	    claim.setClaimAmount(BigDecimal.valueOf(5000000));
	    return claim;
	}

	@Test
	void createClaim_success() {
		when(policyRepository.findById(1L)).thenReturn(Optional.of(activePolicy));
		when(claimRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		ClaimResponse response = claimService.createClaim(buildValidRequest());
		assertNotNull(response);
		assertEquals(ClaimStatus.SUBMITTED, response.getClaimStatus());
		verify(claimRepository, times(1)).save(any());

	}

	@Test
	void createClaim_policyNotFound() {
		when(policyRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> claimService.createClaim(buildValidRequest()));
	}

	@Test
	void createClaim_policyNotActive() {
		activePolicy.setStatus(PolicyStatus.INACTIVE);
		when(policyRepository.findById(1L)).thenReturn(Optional.of(activePolicy));
		assertThrows(ConflictException.class, () -> claimService.createClaim(buildValidRequest()));
	}

	@Test
	void createClaim_invalidAmount() {
		CreateClaimRequest request = buildValidRequest();
		request.setClaimAmount(BigDecimal.ZERO);
		assertThrows(BadRequestException.class, () -> claimService.createClaim(request));
	}
	
	@Test
	void createClaim_invalidAmountNegative() {
	    when(policyRepository.findById(1L)).thenReturn(Optional.of(activePolicy));
	    CreateClaimRequest request = buildValidRequest();
	    request.setClaimAmount(BigDecimal.valueOf(-100));
	    assertThrows(BadRequestException.class, () -> claimService.createClaim(request));
	}
	
	@Test
	void createClaim_shouldThrow_whenDescriptionTooLong() {
	    when(policyRepository.findById(1L)).thenReturn(Optional.of(activePolicy));
	    CreateClaimRequest request = buildValidRequest();
	    String longDesc = "A".repeat(501);
	    request.setDescription(longDesc);
	    assertThrows(BadRequestException.class, () -> claimService.createClaim(request));
	}

	@Test
	void updateClaimStatus_approve_success() {
		Claim claim = buildSubmittedClaim();
		when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
		when(claimRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
		request.setNewStatus(ClaimStatus.APPROVED);
		request.setApprovedAmount(BigDecimal.valueOf(4000000));
		ClaimResponse response = claimService.updateStatus(1L, request);
		assertEquals(ClaimStatus.APPROVED, response.getClaimStatus());
	}

	@Test
	void updateClaimStatus_reject_success() {
		Claim claim = buildSubmittedClaim();
		when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
		when(claimRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
		request.setNewStatus(ClaimStatus.REJECTED);
		ClaimResponse response = claimService.updateStatus(1L, request);
		assertEquals(ClaimStatus.REJECTED, response.getClaimStatus());
	}

	@Test
	void updateClaimStatus_invalidTransition_whenAlreadyApproved() {
		Claim claim = buildSubmittedClaim();
		claim.setClaimStatus(ClaimStatus.APPROVED);
		when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
		UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
		request.setNewStatus(ClaimStatus.REJECTED);
		assertThrows(ConflictException.class, () -> claimService.updateStatus(1L, request));
	}
	
	@Test
	void updateClaimStatus_invalidTransition_whenAlreadyRejected() {
		Claim claim = buildSubmittedClaim();
		claim.setClaimStatus(ClaimStatus.REJECTED);
		when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
		UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
		request.setNewStatus(ClaimStatus.APPROVED);
		assertThrows(ConflictException.class, () -> claimService.updateStatus(1L, request));
	}
	
	@Test
	void updateClaimStatus_whenApproveWithoutAmount() {
	    Claim claim = buildSubmittedClaim();
	    when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
	    UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
	    request.setNewStatus(ClaimStatus.APPROVED);
	    request.setApprovedAmount(null);
	    assertThrows(BadRequestException.class, () -> claimService.updateStatus(1L, request));
	}
	
	@Test
	void updateClaimStatus_whenApprovedAmountZero() {
	    Claim claim = buildSubmittedClaim();
	    when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
	    UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
	    request.setNewStatus(ClaimStatus.APPROVED);
	    request.setApprovedAmount(BigDecimal.ZERO);
	    assertThrows(BadRequestException.class, () -> claimService.updateStatus(1L, request));
	}
	
	@Test
	void update_shouldThrow_whenApprovedAmountExceedsClaimAmount() {
	    Claim claim = buildSubmittedClaim();
	    claim.setClaimAmount(BigDecimal.valueOf(5000000));
	    when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
	    UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
	    request.setNewStatus(ClaimStatus.APPROVED);
	    request.setApprovedAmount(BigDecimal.valueOf(6000000));
	    assertThrows(BadRequestException.class, () -> claimService.updateStatus(1L, request));
	}
}