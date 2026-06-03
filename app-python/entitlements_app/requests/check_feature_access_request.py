"""Pydantic request model for command `CheckFeatureAccess`."""

from pydantic import BaseModel


class CheckFeatureAccessRequest(BaseModel):
    accountId: str
    allowed: bool
    decisionId: str
    evaluatedAt: str
    featureKey: str
    reason: str
    requestedQuantity: int
    subscriptionId: str
    tenantId: str

    @property
    def account_id(self) -> str:
        return self.accountId

    @property
    def decision_id(self) -> str:
        return self.decisionId

    @property
    def evaluated_at(self) -> str:
        return self.evaluatedAt

    @property
    def feature_key(self) -> str:
        return self.featureKey

    @property
    def requested_quantity(self) -> int:
        return self.requestedQuantity

    @property
    def subscription_id(self) -> str:
        return self.subscriptionId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
