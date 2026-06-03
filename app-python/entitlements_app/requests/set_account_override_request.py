"""Pydantic request model for command `SetAccountOverride`."""

from pydantic import BaseModel


class SetAccountOverrideRequest(BaseModel):
    accountId: str
    allowance: int
    createdAt: str
    expiresAt: str | None = None
    featureKey: str
    overrideId: str
    overrideType: str
    reason: str
    tenantId: str

    @property
    def account_id(self) -> str:
        return self.accountId

    @property
    def created_at(self) -> str:
        return self.createdAt

    @property
    def expires_at(self) -> str:
        return self.expiresAt

    @property
    def feature_key(self) -> str:
        return self.featureKey

    @property
    def override_id(self) -> str:
        return self.overrideId

    @property
    def override_type(self) -> str:
        return self.overrideType

    @property
    def tenant_id(self) -> str:
        return self.tenantId
