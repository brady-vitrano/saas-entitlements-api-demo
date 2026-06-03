"""pytest scaffold for RecordUsageService."""

from entitlements_app.services.record_usage_service import RecordUsageService


def test_record_usage_service_module_loads() -> None:
    assert RecordUsageService is not None
