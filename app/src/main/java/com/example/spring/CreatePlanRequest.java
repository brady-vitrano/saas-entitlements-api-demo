package com.example.spring;

/**
 * Request DTO for command CreatePlan.
 */
public record CreatePlanRequest(
    @jakarta.validation.constraints.NotBlank String billingInterval,
    @jakarta.validation.constraints.NotBlank String createdAt,
    @jakarta.validation.constraints.NotBlank String displayName,
    @jakarta.validation.constraints.NotBlank String planCode,
    @jakarta.validation.constraints.NotBlank String planId,
    @jakarta.validation.constraints.NotBlank String status,
    @jakarta.validation.constraints.NotBlank String tenantId
) {}
