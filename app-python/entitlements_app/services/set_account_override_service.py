"""Create an account-specific entitlement override for support or sales operations."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.set_account_override_request import SetAccountOverrideRequest
from entitlements_app.entities import AccountOverride
from entitlements_app.ports import EntitlementsRepository


class SetAccountOverrideService:
    """Service handling Command `SetAccountOverride`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: SetAccountOverrideRequest) -> AccountOverride:
        # spec-driven body for Command SetAccountOverride.
        # intent: Create an account-specific entitlement override for support or sales operations.
        # requires: EntitlementsRepository
        # emits events: AccountOverrideSet
        # return: AccountOverride (entity); construct from request inputs and persist via EntitlementsRepository.saveAccountOverride.
        value = AccountOverride(tenantId=request.tenantId, overrideId=request.overrideId, accountId=request.accountId, featureKey=request.featureKey, overrideType=request.overrideType, allowance=request.allowance, reason=request.reason, expiresAt=request.expiresAt, createdAt=request.createdAt)
        return self.entitlements_repository.saveAccountOverride(value)
