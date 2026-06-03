package com.example.spring;

/**
 * Request DTO for query ListAccountOverrides.
 */
public record ListAccountOverridesQueryRequest(
    String accountId,
    String tenantId
) {}
