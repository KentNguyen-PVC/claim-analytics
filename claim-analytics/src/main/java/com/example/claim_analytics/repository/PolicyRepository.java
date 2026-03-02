package com.example.claim_analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.claim_analytics.entity.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}