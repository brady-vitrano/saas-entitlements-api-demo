package com.example.spring;

/**
 * Request DTO for command AddFeatureToPlan.
 */
public record AddFeatureToPlanRequest(
    int allowance,
    @jakarta.validation.constraints.NotBlank String effectiveAt,
    @jakarta.validation.constraints.NotBlank String enforcementMode,
    @jakarta.validation.constraints.NotBlank String entitlementId,
    @jakarta.validation.constraints.NotBlank String featureKey,
    @jakarta.validation.constraints.NotBlank String planId,
    @jakarta.validation.constraints.NotBlank String tenantId,
    @jakarta.validation.constraints.NotBlank String unit
) {}
