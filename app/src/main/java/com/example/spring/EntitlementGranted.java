package com.example.spring;

/**
 * Event EntitlementGranted.
 */
public record EntitlementGranted(int allowance, String effectiveAt, String enforcementMode, String entitlementId, String featureKey, String planId, String tenantId, String unit) {
}