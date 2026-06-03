"""pytest scaffold for ListAccountAuditEventsQueryService."""

from entitlements_app.services.list_account_audit_events_query_service import ListAccountAuditEventsQueryService


def test_list_account_audit_events_query_service_module_loads() -> None:
    assert ListAccountAuditEventsQueryService is not None
