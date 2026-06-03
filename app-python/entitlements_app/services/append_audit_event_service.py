"""Append an immutable audit event for a subscription or entitlement decision."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.append_audit_event_request import AppendAuditEventRequest
from entitlements_app.entities import AuditEvent
from entitlements_app.ports import EntitlementsRepository


class AppendAuditEventService:
    """Service handling Command `AppendAuditEvent`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: AppendAuditEventRequest) -> AuditEvent:
        # spec-driven body for Command AppendAuditEvent.
        # intent: Append an immutable audit event for a subscription or entitlement decision.
        # requires: EntitlementsRepository
        # return: AuditEvent (entity); construct from request inputs and persist via EntitlementsRepository.saveAuditEvent.
        value = AuditEvent(tenantId=request.tenantId, auditEventId=request.auditEventId, accountId=request.accountId, actorId=request.actorId, eventType=request.eventType, subjectId=request.subjectId, message=request.message, occurredAt=request.occurredAt)
        return self.entitlements_repository.saveAuditEvent(value)
