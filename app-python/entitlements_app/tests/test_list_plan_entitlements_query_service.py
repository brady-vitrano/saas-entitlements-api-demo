"""pytest scaffold for ListPlanEntitlementsQueryService."""

from entitlements_app.services.list_plan_entitlements_query_service import ListPlanEntitlementsQueryService


def test_list_plan_entitlements_query_service_module_loads() -> None:
    assert ListPlanEntitlementsQueryService is not None
