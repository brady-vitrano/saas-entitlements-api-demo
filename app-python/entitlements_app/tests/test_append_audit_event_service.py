"""pytest scaffold for AppendAuditEventService."""

from entitlements_app.services.append_audit_event_service import AppendAuditEventService


def test_append_audit_event_service_module_loads() -> None:
    assert AppendAuditEventService is not None
