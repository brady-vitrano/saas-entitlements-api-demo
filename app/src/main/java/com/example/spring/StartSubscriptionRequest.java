package com.example.spring;

/**
 * Request DTO for command StartSubscription.
 */
public record StartSubscriptionRequest(
    @jakarta.validation.constraints.NotBlank String accountId,
    int currentSeats,
    @jakarta.validation.constraints.NotBlank String planId,
    @jakarta.validation.constraints.NotBlank String renewsAt,
    int seatLimit,
    @jakarta.validation.constraints.NotBlank String startedAt,
    @jakarta.validation.constraints.NotBlank String status,
    @jakarta.validation.constraints.NotBlank String subscriptionId,
    @jakarta.validation.constraints.NotBlank String tenantId
) {}
