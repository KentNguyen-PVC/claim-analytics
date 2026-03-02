package com.example.claim_analytics.controller;

import com.example.claim_analytics.entity.Policy;
import com.example.claim_analytics.enums.PolicyStatus;
import com.example.claim_analytics.repository.PolicyRepository;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClaimControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PolicyRepository policyRepository;
	
	@Test
    void createClaim_shouldReturn201() throws Exception {

        Policy policy = new Policy();
        policy.setStatus(PolicyStatus.ACTIVE);
        policy = policyRepository.save(policy);

        String json = """
        {
          "policyId": %d,
          "claimAmount": 5000000,
          "claimType": "HOSPITALIZATION",
          "description": "Emergency"
        }
        """.formatted(policy.getId());

        mockMvc.perform(post("/api/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimStatus").value("SUBMITTED"));
    }
	
	@Test
	void getClaim_shouldReturn404_whenNotFound() throws Exception {
	    mockMvc.perform(get("/api/claims/999"))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.message").value("Claim not found"));
	}

	@Test
	void fullFlow_create_then_approve() throws Exception {

		Policy policy = new Policy();
		policy.setStatus(PolicyStatus.ACTIVE);
		policy = policyRepository.save(policy);

		String createJson = """
				{
				  "policyId": %d,
				  "claimAmount": 5000000,
				  "claimType": "HOSPITALIZATION",
				  "incidentDate": "2025-01-01T10:00:00Z"
				}
				""".formatted(policy.getId());

		MvcResult result = mockMvc
						.perform(post("/api/claims")
						.contentType(MediaType.APPLICATION_JSON)
						.content(createJson))
						.andExpect(status().isCreated())
						.andReturn();

		String response = result.getResponse().getContentAsString();
		Long claimId = JsonPath.read(response, "$.claimId");

		String approveJson = """
				{
				  "newStatus": "APPROVED",
				  "approvedAmount": 4000000
				}
				""";

		mockMvc.perform(patch("/api/claims/" + claimId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(approveJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.claimStatus").value("APPROVED"));
	}
	
	@Test
	void invalidTransition_shouldReturn400() throws Exception {
	    mockMvc.perform(patch("/api/claims/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "newStatus": "APPROVED",
                  "approvedAmount": 4000000
                }
                """))
        .andExpect(status().isOk());

	    // approve again
	    mockMvc.perform(patch("/api/claims/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "newStatus": "REJECTED"
                }
                """))
        .andExpect(status().isBadRequest());
	}
}