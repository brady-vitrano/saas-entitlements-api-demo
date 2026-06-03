package com.example.spring;

/**
 * Request DTO for query ListSubscriptionUsage.
 */
public record ListSubscriptionUsageQueryRequest(
    String subscriptionId,
    String tenantId
) {}
