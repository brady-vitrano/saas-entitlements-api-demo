package com.example.spring;

/**
 * Caller-visible HTTP body for command AddFeatureToPlan. Path variables and request-context fields
 * are supplied by transport state, not caller JSON.
 */
public record AddFeatureToPlanHttpRequest(
    int allowance,
    @jakarta.validation.constraints.NotBlank String effectiveAt,
    @jakarta.validation.constraints.NotBlank String enforcementMode,
    @jakarta.validation.constraints.NotBlank String entitlementId,
    @jakarta.validation.constraints.NotBlank String featureKey,
    @jakarta.validation.constraints.NotBlank String tenantId,
    @jakarta.validation.constraints.NotBlank String unit
) {}
