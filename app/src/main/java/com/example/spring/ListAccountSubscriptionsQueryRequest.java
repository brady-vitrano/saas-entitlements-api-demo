package com.example.spring;

/**
 * Request DTO for query ListAccountSubscriptions.
 */
public record ListAccountSubscriptionsQueryRequest(
    String accountId,
    String tenantId
) {}
