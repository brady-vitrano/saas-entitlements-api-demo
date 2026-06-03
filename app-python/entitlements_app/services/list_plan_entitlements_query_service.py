"""List entitlements granted by a plan."""

from decimal import Decimal

from entitlements_app.requests.list_plan_entitlements_query_request import ListPlanEntitlementsQueryRequest
from entitlements_app.entities import Entitlement
from entitlements_app.ports import EntitlementsRepository


class ListPlanEntitlementsQueryService:
    """Service handling Query `ListPlanEntitlements`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: ListPlanEntitlementsQueryRequest) -> list[Entitlement]:
        # spec-driven body for Query ListPlanEntitlements.
        # intent: List entitlements granted by a plan.
        # reads: EntitlementsRepository.listEntitlementsByPlan via `entitlements`.
        return self.entitlements_repository.listEntitlementsByPlan(request.planId)
