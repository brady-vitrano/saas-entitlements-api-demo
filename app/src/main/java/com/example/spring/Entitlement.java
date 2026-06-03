package com.example.spring;


/**
 * Feature allowance granted by a subscription plan.
 * <p>Identity field: entitlementId
 */
public record Entitlement(
        String tenantId,
        String entitlementId,
        String planId,
        String featureKey,
        int allowance,
        String unit,
        String enforcementMode,
        String effectiveAt
) {
}