package com.example.spring;


/**
 * Authorization and entitlement decision returned to product code.
 * <p>Identity field: decisionId
 */
public record PolicyCheckResult(
        String tenantId,
        String decisionId,
        String accountId,
        String subscriptionId,
        String featureKey,
        int requestedQuantity,
        boolean allowed,
        String reason,
        String evaluatedAt
) {
}