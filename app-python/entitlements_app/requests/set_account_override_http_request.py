"""Caller-visible HTTP body for command `SetAccountOverride`.

Request-context fields are supplied by authenticated transport state, not caller JSON.
"""

from pydantic import BaseModel


class SetAccountOverrideHttpRequest(BaseModel):
    allowance: int
    createdAt: str
    expiresAt: str | None = None
    featureKey: str
    overrideId: str
    overrideType: str
    reason: str
    tenantId: str

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
