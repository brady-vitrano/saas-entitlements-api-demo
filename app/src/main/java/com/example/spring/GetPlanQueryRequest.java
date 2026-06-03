package com.example.spring;

/**
 * Request DTO for query GetPlan.
 */
public record GetPlanQueryRequest(
    String planId,
    String tenantId
) {}
