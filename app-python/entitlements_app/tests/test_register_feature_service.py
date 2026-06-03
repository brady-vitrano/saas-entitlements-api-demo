"""pytest scaffold for RegisterFeatureService."""

from entitlements_app.services.register_feature_service import RegisterFeatureService


def test_register_feature_service_module_loads() -> None:
    assert RegisterFeatureService is not None
