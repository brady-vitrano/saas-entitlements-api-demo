"""Pydantic request model for command `AppendAuditEvent`."""

from pydantic import BaseModel


class AppendAuditEventRequest(BaseModel):
    accountId: str
    actorId: str
    auditEventId: str
    eventType: str
    message: str
    occurredAt: str
    subjectId: str
    tenantId: str

    @property
    def account_id(self) -> str:
        return self.accountId

    @property
    def actor_id(self) -> str:
        return self.actorId

    @property
    def audit_event_id(self) -> str:
        return self.auditEventId

    @property
    def event_type(self) -> str:
        return self.eventType

    @property
    def occurred_at(self) -> str:
        return self.occurredAt

    @property
    def subject_id(self) -> str:
        return self.subjectId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
