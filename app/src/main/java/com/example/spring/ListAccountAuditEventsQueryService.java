package com.example.spring;

import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * List audit events for an account.
 */
@Service
public class ListAccountAuditEventsQueryService {

    private final EntitlementsRepository entitlementsRepository;

    public ListAccountAuditEventsQueryService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<AuditEvent> handle(ListAccountAuditEventsQueryRequest request) {
        // spec-driven body for Query ListAccountAuditEvents.
        // intent: List audit events for an account.
        // reads: EntitlementsRepository.listAuditEventsByAccount via `auditEvents`.
        return this.entitlementsRepository.listAuditEventsByAccount(request.accountId());
    }
}
