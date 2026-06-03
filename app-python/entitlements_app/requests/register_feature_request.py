"""Pydantic request model for command `RegisterFeature`."""

from pydantic import BaseModel


class RegisterFeatureRequest(BaseModel):
    active: bool
    defaultUnit: str
    displayName: str
    featureKey: str
    tenantId: str
    valueType: str

    @property
    def default_unit(self) -> str:
        return self.defaultUnit

    @property
    def display_name(self) -> str:
        return self.displayName

    @property
    def feature_key(self) -> str:
        return self.featureKey

    @property
    def tenant_id(self) -> str:
        return self.tenantId

    @property
    def value_type(self) -> str:
        return self.valueType
