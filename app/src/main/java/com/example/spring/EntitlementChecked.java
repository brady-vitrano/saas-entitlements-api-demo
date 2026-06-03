package com.example.spring;

/**
 * Event EntitlementChecked.
 */
public record EntitlementChecked(String accountId, boolean allowed, String decisionId, String evaluatedAt, String featureKey, String reason, int requestedQuantity, String subscriptionId, String tenantId) {
}