"""pytest scaffold for StartSubscriptionService."""

from entitlements_app.services.start_subscription_service import StartSubscriptionService


def test_start_subscription_service_module_loads() -> None:
    assert StartSubscriptionService is not None
