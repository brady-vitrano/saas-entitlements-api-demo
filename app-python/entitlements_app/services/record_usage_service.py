"""Record metered usage for a subscription feature during a billing period."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.record_usage_request import RecordUsageRequest
from entitlements_app.entities import UsageMeter
from entitlements_app.ports import EntitlementsRepository


class RecordUsageService:
    """Service handling Command `RecordUsage`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: RecordUsageRequest) -> UsageMeter:
        # spec-driven body for Command RecordUsage.
        # intent: Record metered usage for a subscription feature during a billing period.
        # requires: EntitlementsRepository
        # emits events: UsageRecorded
        # return: UsageMeter (entity); construct from request inputs and persist via EntitlementsRepository.saveUsageMeter.
        value = UsageMeter(tenantId=request.tenantId, usageId=request.usageId, subscriptionId=request.subscriptionId, accountId=request.accountId, featureKey=request.featureKey, quantity=request.quantity, periodStart=request.periodStart, periodEnd=request.periodEnd, recordedAt=request.recordedAt)
        return self.entitlements_repository.saveUsageMeter(value)
