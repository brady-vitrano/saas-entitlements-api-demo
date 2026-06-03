package com.example.spring;

/**
 * Request DTO for query ListPlanEntitlements.
 */
public record ListPlanEntitlementsQueryRequest(
    String planId,
    String tenantId
) {}
