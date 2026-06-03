"""Record and return an entitlement authorization decision for a feature request."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.check_feature_access_request import CheckFeatureAccessRequest
from entitlements_app.entities import PolicyCheckResult
from entitlements_app.ports import EntitlementsRepository


class CheckFeatureAccessService:
    """Service handling Command `CheckFeatureAccess`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: CheckFeatureAccessRequest) -> PolicyCheckResult:
        # spec-driven body for Command CheckFeatureAccess.
        # intent: Record and return an entitlement authorization decision for a feature request.
        # requires: EntitlementsRepository
        # emits events: EntitlementChecked
        # return: PolicyCheckResult (entity); construct from request inputs and persist via EntitlementsRepository.savePolicyCheckResult.
        value = PolicyCheckResult(tenantId=request.tenantId, decisionId=request.decisionId, accountId=request.accountId, subscriptionId=request.subscriptionId, featureKey=request.featureKey, requestedQuantity=request.requestedQuantity, allowed=request.allowed, reason=request.reason, evaluatedAt=request.evaluatedAt)
        return self.entitlements_repository.savePolicyCheckResult(value)
