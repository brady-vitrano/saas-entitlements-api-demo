"""Pydantic request model for query `ListSubscriptionUsage`."""

from pydantic import BaseModel


class ListSubscriptionUsageQueryRequest(BaseModel):
    subscriptionId: str
    tenantId: str

    @property
    def subscription_id(self) -> str:
        return self.subscriptionId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
