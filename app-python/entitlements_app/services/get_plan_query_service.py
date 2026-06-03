"""Return one subscription plan by id."""

from decimal import Decimal

from entitlements_app.requests.get_plan_query_request import GetPlanQueryRequest
from entitlements_app.entities import Plan
from entitlements_app.ports import EntitlementsRepository


class GetPlanQueryService:
    """Service handling Query `GetPlan`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: GetPlanQueryRequest) -> Plan:
        # spec-driven body for Query GetPlan.
        # intent: Return one subscription plan by id.
        # reads: EntitlementsRepository.findPlan via `plan`.
        return self.entitlements_repository.findPlan(request.planId)
