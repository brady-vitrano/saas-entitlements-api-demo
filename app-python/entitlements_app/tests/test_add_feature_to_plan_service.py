"""pytest scaffold for AddFeatureToPlanService."""

from entitlements_app.services.add_feature_to_plan_service import AddFeatureToPlanService


def test_add_feature_to_plan_service_module_loads() -> None:
    assert AddFeatureToPlanService is not None
