package com.example.spring;

/**
 * Request DTO for query ListAccountAuditEvents.
 */
public record ListAccountAuditEventsQueryRequest(
    String accountId,
    String tenantId
) {}
