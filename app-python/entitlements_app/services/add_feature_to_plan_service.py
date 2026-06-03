"""Grant a feature allowance to a plan."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.add_feature_to_plan_request import AddFeatureToPlanRequest
from entitlements_app.entities import Entitlement
from entitlements_app.ports import EntitlementsRepository


class AddFeatureToPlanService:
    """Service handling Command `AddFeatureToPlan`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: AddFeatureToPlanRequest) -> Entitlement:
        # spec-driven body for Command AddFeatureToPlan.
        # intent: Grant a feature allowance to a plan.
        # requires: EntitlementsRepository
        # emits events: EntitlementGranted
        # return: Entitlement (entity); construct from request inputs and persist via EntitlementsRepository.saveEntitlement.
        value = Entitlement(tenantId=request.tenantId, entitlementId=request.entitlementId, planId=request.planId, featureKey=request.featureKey, allowance=request.allowance, unit=request.unit, enforcementMode=request.enforcementMode, effectiveAt=request.effectiveAt)
        return self.entitlements_repository.saveEntitlement(value)
