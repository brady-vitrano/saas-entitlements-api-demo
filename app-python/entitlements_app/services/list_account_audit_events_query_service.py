"""List audit events for an account."""

from decimal import Decimal

from entitlements_app.requests.list_account_audit_events_query_request import ListAccountAuditEventsQueryRequest
from entitlements_app.entities import AuditEvent
from entitlements_app.ports import EntitlementsRepository


class ListAccountAuditEventsQueryService:
    """Service handling Query `ListAccountAuditEvents`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: ListAccountAuditEventsQueryRequest) -> list[AuditEvent]:
        # spec-driven body for Query ListAccountAuditEvents.
        # intent: List audit events for an account.
        # reads: EntitlementsRepository.listAuditEventsByAccount via `auditEvents`.
        return self.entitlements_repository.listAuditEventsByAccount(request.accountId)
