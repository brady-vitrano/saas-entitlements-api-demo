package com.example.spring;


/**
 * Product capability that can be granted through a plan or override.
 * <p>Identity field: featureKey
 */
public record Feature(
        String tenantId,
        String featureKey,
        String displayName,
        String valueType,
        String defaultUnit,
        boolean active
) {
}