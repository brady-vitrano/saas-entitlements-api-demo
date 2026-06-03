package com.example.spring;

/**
 * Caller-visible HTTP body for command SetAccountOverride. Path variables and request-context fields
 * are supplied by transport state, not caller JSON.
 */
public record SetAccountOverrideHttpRequest(
    int allowance,
    @jakarta.validation.constraints.NotBlank String createdAt,
    String expiresAt,
    @jakarta.validation.constraints.NotBlank String featureKey,
    @jakarta.validation.constraints.NotBlank String overrideId,
    @jakarta.validation.constraints.NotBlank String overrideType,
    @jakarta.validation.constraints.NotBlank String reason,
    @jakarta.validation.constraints.NotBlank String tenantId
) {}
