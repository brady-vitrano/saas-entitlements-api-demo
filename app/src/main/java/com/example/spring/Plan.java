package com.example.spring;


/**
 * Commercial subscription plan offered to accounts.
 * <p>Identity field: planId
 */
public record Plan(
        String tenantId,
        String planId,
        String planCode,
        String displayName,
        String billingInterval,
        String status,
        String createdAt
) {
}