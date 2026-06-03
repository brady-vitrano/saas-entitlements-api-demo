package com.example.spring;


/**
 * Metered usage rollup for one subscription and feature.
 * <p>Identity field: usageId
 */
public record UsageMeter(
        String tenantId,
        String usageId,
        String subscriptionId,
        String accountId,
        String featureKey,
        int quantity,
        String periodStart,
        String periodEnd,
        String recordedAt
) {
}