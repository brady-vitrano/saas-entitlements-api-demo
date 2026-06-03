package com.example.spring;

/**
 * Request DTO for command RecordUsage.
 */
public record RecordUsageRequest(
    @jakarta.validation.constraints.NotBlank String accountId,
    @jakarta.validation.constraints.NotBlank String featureKey,
    @jakarta.validation.constraints.NotBlank String periodEnd,
    @jakarta.validation.constraints.NotBlank String periodStart,
    int quantity,
    @jakarta.validation.constraints.NotBlank String recordedAt,
    @jakarta.validation.constraints.NotBlank String subscriptionId,
    @jakarta.validation.constraints.NotBlank String tenantId,
    @jakarta.validation.constraints.NotBlank String usageId
) {}
