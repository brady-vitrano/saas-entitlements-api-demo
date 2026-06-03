"""Pydantic request model for query `GetPlan`."""

from pydantic import BaseModel


class GetPlanQueryRequest(BaseModel):
    planId: str
    tenantId: str

    @property
    def plan_id(self) -> str:
        return self.planId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
