"""Pydantic request model for command `AddFeatureToPlan`."""

from pydantic import BaseModel


class AddFeatureToPlanRequest(BaseModel):
    allowance: int
    effectiveAt: str
    enforcementMode: str
    entitlementId: str
    featureKey: str
    planId: str
    tenantId: str
    unit: str

    @property
    def effective_at(self) -> str:
        return self.effectiveAt

    @property
    def enforcement_mode(self) -> str:
        return self.enforcementMode

    @property
    def entitlement_id(self) -> str:
        return self.entitlementId

    @property
    def feature_key(self) -> str:
        return self.featureKey

    @property
    def plan_id(self) -> str:
        return self.planId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
