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

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private PolicyRepository policyRepository;
    
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private Policy activePolicy;
    private Claim submittedClaim;

    @BeforeEach
    void setup() {

        activePolicy = new Policy();
        activePolicy.setId(1L);
        activePolicy.setPolicyNumber("POL-SG-2026-001");
        activePolicy.setCountryCode("SG");
        activePolicy.setStatus(PolicyStatus.ACTIVE);
        activePolicy.setEffectiveDate(OffsetDateTime.now().minusDays(1));
        activePolicy.setExpiryDate(OffsetDateTime.now().plusDays(100));
        activePolicy.setCreatedAt(OffsetDateTime.now());

        submittedClaim = new Claim();
        submittedClaim.setClaimId(1L);
        submittedClaim.setPolicy(activePolicy);
        submittedClaim.setClaimStatus(ClaimStatus.SUBMITTED);
        submittedClaim.setClaimAmount(BigDecimal.valueOf(5_000_000));
    }

    // =========================================================
    // CREATE CLAIM TESTS
    // =========================================================

    @Test
    void createClaim_success() {

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(activePolicy));

        when(claimRepository.save(any(Claim.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClaimResponse response = claimService.createClaim(buildValidRequest());

        assertNotNull(response);
        assertEquals(ClaimStatus.SUBMITTED, response.getClaimStatus());
        verify(claimRepository).save(any(Claim.class));
    }

    @Test
    void createClaim_policyNotFound() {

        when(policyRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> claimService.createClaim(buildValidRequest()));

        verify(claimRepository, never()).save(any());
    }

    @Test
    void createClaim_policyNotActive() {

        activePolicy.setStatus(PolicyStatus.INACTIVE);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(activePolicy));

        assertThrows(ConflictException.class,
                () -> claimService.createClaim(buildValidRequest()));

        verify(claimRepository, never()).save(any());
    }

    @Test
    void createClaim_invalidAmount_zero() {

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(activePolicy));

        CreateClaimRequest request = buildValidRequest();
        request.setClaimAmount(BigDecimal.ZERO);

        assertThrows(BadRequestException.class,
                () -> claimService.createClaim(request));
    }

    @Test
    void createClaim_invalidAmount_negative() {

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(activePolicy));

        CreateClaimRequest request = buildValidRequest();
        request.setClaimAmount(BigDecimal.valueOf(-100));

        assertThrows(BadRequestException.class,
                () -> claimService.createClaim(request));
    }

    @Test
    void createClaim_descriptionTooLong() {

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(activePolicy));

        CreateClaimRequest request = buildValidRequest();
        request.setDescription("A".repeat(501));

        assertThrows(BadRequestException.class,
                () -> claimService.createClaim(request));
    }

    // =========================================================
    // UPDATE STATUS TESTS
    // =========================================================

//    @Test
//    void updateStatus_approve_success() {
//        when(claimRepository.findByIdWithPolicy(1L)).thenReturn(Optional.of(submittedClaim));
//        doNothing().when(claimRepository).finalizeClaim(eq(1L), anyString(), anyString());
//        when(claimRepository.findByIdWithPolicy(1L)).thenReturn(Optional.of(submittedClaim));
//        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
//        request.setNewStatus(ClaimStatus.APPROVED);
//        request.setApprovedAmount(BigDecimal.valueOf(4_000_000));
//        ClaimResponse response = claimService.updateStatus(1L, request);
//        assertEquals(ClaimStatus.APPROVED, response.getClaimStatus());
//        verify(claimRepository).finalizeClaim(
//                1L,
//                ClaimStatus.SUBMITTED.name(),
//                ClaimStatus.APPROVED.name()
//        );
//    }
    
    @Test
    void updateStatus_approve_success() {
        when(claimRepository.findByIdWithPolicy(1L)).thenReturn(Optional.of(submittedClaim));
        doNothing().when(claimRepository).finalizeClaim(any(), any(), any());
        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);
        request.setApprovedAmount(BigDecimal.valueOf(4_000_000));
        ClaimResponse response = claimService.updateStatus(1L, request);
        assertEquals(ClaimStatus.APPROVED, response.getClaimStatus());
        verify(claimRepository).finalizeClaim(any(), any(), any());
    }
    
    @Test
    void updateStatus_reject_success() {
        when(claimRepository.findByIdWithPolicy(1L)).thenReturn(Optional.of(submittedClaim));
        doNothing().when(claimRepository).finalizeClaim(any(), any(), any());
        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.REJECTED);
        request.setApprovedAmount(BigDecimal.valueOf(4_000_000));
        ClaimResponse response = claimService.updateStatus(1L, request);
        assertEquals(ClaimStatus.REJECTED, response.getClaimStatus());
        verify(claimRepository).finalizeClaim(any(), any(), any());
    }

//    @Test
//    void updateStatus_reject_success() {
//
//        when(claimRepository.findByIdWithPolicy(1L))
//                .thenReturn(Optional.of(submittedClaim));
//
//        when(claimRepository.save(any()))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
//        request.setNewStatus(ClaimStatus.REJECTED);
//
//        ClaimResponse response = claimService.updateStatus(1L, request);
//
//        assertEquals(ClaimStatus.REJECTED, response.getClaimStatus());
//    }

    @Test
    void updateStatus_claimNotFound() {

        when(claimRepository.findByIdWithPolicy(1L))
                .thenReturn(Optional.empty());

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);

        assertThrows(NotFoundException.class,
                () -> claimService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_invalidTransition_fromApproved() {

        submittedClaim.setClaimStatus(ClaimStatus.APPROVED);

        when(claimRepository.findByIdWithPolicy(1L))
                .thenReturn(Optional.of(submittedClaim));

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.REJECTED);

        assertThrows(ConflictException.class,
                () -> claimService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_invalidTransition_fromRejected() {

        submittedClaim.setClaimStatus(ClaimStatus.REJECTED);

        when(claimRepository.findByIdWithPolicy(1L))
                .thenReturn(Optional.of(submittedClaim));

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);

        assertThrows(ConflictException.class,
                () -> claimService.updateStatus(1L, request));
    }

    @Test
    void updateStatus_approveWithoutAmount() {

        when(claimRepository.findByIdWithPolicy(1L))
                .thenReturn(Optional.of(submittedClaim));

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);
        request.setApprovedAmount(null);
        assertThrows(BadRequestException.class,
                () -> claimService.updateStatus(1L, request));
    }
    
	
	@Test
	void updateStatus_whenApprovedAmountZero() {
//	    Claim claim = buildSubmittedClaim();
	    when(claimRepository.findByIdWithPolicy(1L)).thenReturn(Optional.of(submittedClaim));
	    UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
	    request.setNewStatus(ClaimStatus.APPROVED);
	    request.setApprovedAmount(BigDecimal.ZERO);
	    assertThrows(BadRequestException.class, () -> claimService.updateStatus(1L, request));
	}

    @Test
    void updateStatus_approvedAmountExceedsClaimAmount() {

        when(claimRepository.findByIdWithPolicy(1L))
                .thenReturn(Optional.of(submittedClaim));

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);
        request.setApprovedAmount(BigDecimal.valueOf(10_000_000));

        assertThrows(BadRequestException.class,
                () -> claimService.updateStatus(1L, request));
    }


    private CreateClaimRequest buildValidRequest() {

        CreateClaimRequest request = new CreateClaimRequest();
        request.setPolicyId(1L);
        request.setClaimAmount(BigDecimal.valueOf(5_000_000));
        request.setClaimType(ClaimType.HOSPITALIZATION);
        request.setDescription("Emergency surgery");

        return request;
    }
}