package com.example.spring;

/**
 * Event UsageRecorded.
 */
public record UsageRecorded(String accountId, String featureKey, int quantity, String recordedAt, String subscriptionId, String tenantId, String usageId) {
}