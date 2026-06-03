"""pytest scaffold for CheckFeatureAccessService."""

from entitlements_app.services.check_feature_access_service import CheckFeatureAccessService


def test_check_feature_access_service_module_loads() -> None:
    assert CheckFeatureAccessService is not None
