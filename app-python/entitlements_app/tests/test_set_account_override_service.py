"""pytest scaffold for SetAccountOverrideService."""

from entitlements_app.services.set_account_override_service import SetAccountOverrideService


def test_set_account_override_service_module_loads() -> None:
    assert SetAccountOverrideService is not None
