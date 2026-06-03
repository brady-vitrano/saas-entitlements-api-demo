package com.example.spring;


/**
 * Account subscription to a commercial plan.
 * <p>Identity field: subscriptionId
 */
public record Subscription(
        String tenantId,
        String subscriptionId,
        String accountId,
        String planId,
        String status,
        int seatLimit,
        int currentSeats,
        String startedAt,
        String renewsAt
) {
}