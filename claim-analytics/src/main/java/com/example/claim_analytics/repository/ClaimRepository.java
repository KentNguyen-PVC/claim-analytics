package com.example.claim_analytics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.claim_analytics.entity.Claim;
import com.example.claim_analytics.enums.ClaimStatus;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long>, ClaimRepositoryCustom {
	
	List<Claim> findByPolicy_Id(Long policyId);

	List<Claim> findByPolicy_IdAndClaimStatus(Long policyId, ClaimStatus status);
	
	Page<Claim> findByPolicy_IdAndClaimStatus(
	        Long policyId,
	        ClaimStatus status,
	        Pageable pageable
	);

	@Query("""
			    SELECT c FROM Claim c
			    JOIN FETCH c.policy
			    WHERE c.claimId = :id
			""")
	Optional<Claim> findByIdWithPolicy(Long id);
}