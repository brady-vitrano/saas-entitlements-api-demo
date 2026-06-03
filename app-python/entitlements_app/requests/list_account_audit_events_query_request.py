"""Pydantic request model for query `ListAccountAuditEvents`."""

from pydantic import BaseModel


class ListAccountAuditEventsQueryRequest(BaseModel):
    accountId: str
    tenantId: str

    @property
    def account_id(self) -> str:
        return self.accountId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
