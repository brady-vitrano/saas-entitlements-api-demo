"""Pydantic request model for command `CreatePlan`."""

from pydantic import BaseModel


class CreatePlanRequest(BaseModel):
    billingInterval: str
    createdAt: str
    displayName: str
    planCode: str
    planId: str
    status: str
    tenantId: str

    @property
    def billing_interval(self) -> str:
        return self.billingInterval

    @property
    def created_at(self) -> str:
        return self.createdAt

    @property
    def display_name(self) -> str:
        return self.displayName

    @property
    def plan_code(self) -> str:
        return self.planCode

    @property
    def plan_id(self) -> str:
        return self.planId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
