"""pytest scaffold for CreatePlanService."""

from entitlements_app.services.create_plan_service import CreatePlanService


def test_create_plan_service_module_loads() -> None:
    assert CreatePlanService is not None
