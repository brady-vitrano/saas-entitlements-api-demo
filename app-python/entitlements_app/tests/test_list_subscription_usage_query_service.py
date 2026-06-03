"""pytest scaffold for ListSubscriptionUsageQueryService."""

from entitlements_app.services.list_subscription_usage_query_service import ListSubscriptionUsageQueryService


def test_list_subscription_usage_query_service_module_loads() -> None:
    assert ListSubscriptionUsageQueryService is not None
