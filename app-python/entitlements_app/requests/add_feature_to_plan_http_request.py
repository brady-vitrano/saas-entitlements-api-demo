"""Caller-visible HTTP body for command `AddFeatureToPlan`.

Request-context fields are supplied by authenticated transport state, not caller JSON.
"""

from pydantic import BaseModel


class AddFeatureToPlanHttpRequest(BaseModel):
    allowance: int
    effectiveAt: str
    enforcementMode: str
    entitlementId: str
    featureKey: str
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
    def tenant_id(self) -> str:
        return self.tenantId
