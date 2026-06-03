"""pytest scaffold for GetPlanQueryService."""

from entitlements_app.services.get_plan_query_service import GetPlanQueryService


def test_get_plan_query_service_module_loads() -> None:
    assert GetPlanQueryService is not None
