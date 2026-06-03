package com.example.spring;


/**
 * Account-specific entitlement override that can raise, lower, grant, or deny access.
 * <p>Identity field: overrideId
 */
public record AccountOverride(
        String tenantId,
        String overrideId,
        String accountId,
        String featureKey,
        String overrideType,
        int allowance,
        String reason,
        String expiresAt,
        String createdAt
) {
}