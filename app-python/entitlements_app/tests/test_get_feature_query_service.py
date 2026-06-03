"""pytest scaffold for GetFeatureQueryService."""

from entitlements_app.services.get_feature_query_service import GetFeatureQueryService


def test_get_feature_query_service_module_loads() -> None:
    assert GetFeatureQueryService is not None
