package com.example.spring;

/**
 * Event SubscriptionStarted.
 */
public record SubscriptionStarted(String accountId, String planId, int seatLimit, String startedAt, String subscriptionId, String tenantId) {
}