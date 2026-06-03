"""Pydantic request model for query `GetFeature`."""

from pydantic import BaseModel


class GetFeatureQueryRequest(BaseModel):
    featureKey: str
    tenantId: str

    @property
    def feature_key(self) -> str:
        return self.featureKey

    @property
    def tenant_id(self) -> str:
        return self.tenantId
