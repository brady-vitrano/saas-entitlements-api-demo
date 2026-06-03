package com.example.spring;

/**
 * Event AccountOverrideSet.
 */
public record AccountOverrideSet(String accountId, int allowance, String createdAt, String featureKey, String overrideId, String overrideType, String tenantId) {
}