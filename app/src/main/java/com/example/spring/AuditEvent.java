package com.example.spring;


/**
 * Immutable audit event for entitlement decisions and subscription changes.
 * <p>Identity field: auditEventId
 */
public record AuditEvent(
        String tenantId,
        String auditEventId,
        String accountId,
        String actorId,
        String eventType,
        String subjectId,
        String message,
        String occurredAt
) {
}