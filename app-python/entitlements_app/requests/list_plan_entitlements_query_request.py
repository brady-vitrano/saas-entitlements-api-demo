"""Pydantic request model for query `ListPlanEntitlements`."""

from pydantic import BaseModel


class ListPlanEntitlementsQueryRequest(BaseModel):
    planId: str
    tenantId: str

    @property
    def plan_id(self) -> str:
        return self.planId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
