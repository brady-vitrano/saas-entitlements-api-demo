"""Register a feature key that can be attached to plans and checked at runtime."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.register_feature_request import RegisterFeatureRequest
from entitlements_app.entities import Feature
from entitlements_app.ports import EntitlementsRepository


class RegisterFeatureService:
    """Service handling Command `RegisterFeature`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: RegisterFeatureRequest) -> Feature:
        # spec-driven body for Command RegisterFeature.
        # intent: Register a feature key that can be attached to plans and checked at runtime.
        # requires: EntitlementsRepository
        # return: Feature (entity); construct from request inputs and persist via EntitlementsRepository.saveFeature.
        value = Feature(tenantId=request.tenantId, featureKey=request.featureKey, displayName=request.displayName, valueType=request.valueType, defaultUnit=request.defaultUnit, active=request.active)
        return self.entitlements_repository.saveFeature(value)
