"""Create a subscription plan that can later grant feature entitlements."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.create_plan_request import CreatePlanRequest
from entitlements_app.entities import Plan
from entitlements_app.ports import EntitlementsRepository


class CreatePlanService:
    """Service handling Command `CreatePlan`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: CreatePlanRequest) -> Plan:
        # spec-driven body for Command CreatePlan.
        # intent: Create a subscription plan that can later grant feature entitlements.
        # requires: EntitlementsRepository
        # return: Plan (entity); construct from request inputs and persist via EntitlementsRepository.savePlan.
        value = Plan(tenantId=request.tenantId, planId=request.planId, planCode=request.planCode, displayName=request.displayName, billingInterval=request.billingInterval, status=request.status, createdAt=request.createdAt)
        return self.entitlements_repository.savePlan(value)
