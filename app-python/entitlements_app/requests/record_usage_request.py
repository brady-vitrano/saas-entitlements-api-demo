"""Pydantic request model for command `RecordUsage`."""

from pydantic import BaseModel


class RecordUsageRequest(BaseModel):
    accountId: str
    featureKey: str
    periodEnd: str
    periodStart: str
    quantity: int
    recordedAt: str
    subscriptionId: str
    tenantId: str
    usageId: str

    @property
    def account_id(self) -> str:
        return self.accountId

    @property
    def feature_key(self) -> str:
        return self.featureKey

    @property
    def period_end(self) -> str:
        return self.periodEnd

    @property
    def period_start(self) -> str:
        return self.periodStart

    @property
    def recorded_at(self) -> str:
        return self.recordedAt

    @property
    def subscription_id(self) -> str:
        return self.subscriptionId

    @property
    def tenant_id(self) -> str:
        return self.tenantId

    @property
    def usage_id(self) -> str:
        return self.usageId
