package com.example.spring;

/**
 * Request DTO for command AppendAuditEvent.
 */
public record AppendAuditEventRequest(
    @jakarta.validation.constraints.NotBlank String accountId,
    @jakarta.validation.constraints.NotBlank String actorId,
    @jakarta.validation.constraints.NotBlank String auditEventId,
    @jakarta.validation.constraints.NotBlank String eventType,
    @jakarta.validation.constraints.NotBlank String message,
    @jakarta.validation.constraints.NotBlank String occurredAt,
    @jakarta.validation.constraints.NotBlank String subjectId,
    @jakarta.validation.constraints.NotBlank String tenantId
) {}
