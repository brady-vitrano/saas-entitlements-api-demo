package com.example.spring;

/**
 * Request DTO for command CheckFeatureAccess.
 */
public record CheckFeatureAccessRequest(
    @jakarta.validation.constraints.NotBlank String accountId,
    boolean allowed,
    @jakarta.validation.constraints.NotBlank String decisionId,
    @jakarta.validation.constraints.NotBlank String evaluatedAt,
    @jakarta.validation.constraints.NotBlank String featureKey,
    @jakarta.validation.constraints.NotBlank String reason,
    int requestedQuantity,
    @jakarta.validation.constraints.NotBlank String subscriptionId,
    @jakarta.validation.constraints.NotBlank String tenantId
) {}
