package com.example.spring;

/**
 * Request DTO for command SetAccountOverride.
 */
public record SetAccountOverrideRequest(
    @jakarta.validation.constraints.NotBlank String accountId,
    int allowance,
    @jakarta.validation.constraints.NotBlank String createdAt,
    String expiresAt,
    @jakarta.validation.constraints.NotBlank String featureKey,
    @jakarta.validation.constraints.NotBlank String overrideId,
    @jakarta.validation.constraints.NotBlank String overrideType,
    @jakarta.validation.constraints.NotBlank String reason,
    @jakarta.validation.constraints.NotBlank String tenantId
) {}
