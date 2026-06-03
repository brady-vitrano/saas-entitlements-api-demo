"""Pydantic request model for query `ListAccountSubscriptions`."""

from pydantic import BaseModel


class ListAccountSubscriptionsQueryRequest(BaseModel):
    accountId: str
    tenantId: str

    @property
    def account_id(self) -> str:
        return self.accountId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
