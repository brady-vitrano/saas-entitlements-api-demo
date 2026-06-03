package com.example.spring;

/**
 * Request DTO for query GetFeature.
 */
public record GetFeatureQueryRequest(
    String featureKey,
    String tenantId
) {}
