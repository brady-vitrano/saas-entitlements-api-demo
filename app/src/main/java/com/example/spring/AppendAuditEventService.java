package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Append an immutable audit event for a subscription or entitlement decision.
 */
@Service
public class AppendAuditEventService {

    private final EntitlementsRepository entitlementsRepository;

    public AppendAuditEventService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional
    public AuditEvent handle(AppendAuditEventRequest request) {
        // spec-driven body for AppendAuditEvent.
        // intent: Append an immutable audit event for a subscription or entitlement decision.
        // requires: EntitlementsRepository
        // return: AuditEvent (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.saveAuditEvent.
        AuditEvent value = new AuditEvent(request.tenantId(), request.auditEventId(), request.accountId(), request.actorId(), request.eventType(), request.subjectId(), request.message(), request.occurredAt());
        return this.entitlementsRepository.saveAuditEvent(value);
    }
}