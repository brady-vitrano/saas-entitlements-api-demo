"""pytest scaffold for ListAccountOverridesQueryService."""

from entitlements_app.services.list_account_overrides_query_service import ListAccountOverridesQueryService


def test_list_account_overrides_query_service_module_loads() -> None:
    assert ListAccountOverridesQueryService is not None
