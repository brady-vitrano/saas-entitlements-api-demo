"""Return one registered feature by feature key."""

from decimal import Decimal

from entitlements_app.requests.get_feature_query_request import GetFeatureQueryRequest
from entitlements_app.entities import Feature
from entitlements_app.ports import EntitlementsRepository


class GetFeatureQueryService:
    """Service handling Query `GetFeature`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: GetFeatureQueryRequest) -> Feature:
        # spec-driven body for Query GetFeature.
        # intent: Return one registered feature by feature key.
        # reads: EntitlementsRepository.findFeature via `feature`.
        return self.entitlements_repository.findFeature(request.featureKey)
