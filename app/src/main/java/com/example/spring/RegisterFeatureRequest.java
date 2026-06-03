package com.example.spring;

/**
 * Request DTO for command RegisterFeature.
 */
public record RegisterFeatureRequest(
    boolean active,
    @jakarta.validation.constraints.NotBlank String defaultUnit,
    @jakarta.validation.constraints.NotBlank String displayName,
    @jakarta.validation.constraints.NotBlank String featureKey,
    @jakarta.validation.constraints.NotBlank String tenantId,
    @jakarta.validation.constraints.NotBlank String valueType
) {}
