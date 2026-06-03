"""Pydantic models for spec events."""

from pydantic import BaseModel



class AccountOverrideSet(BaseModel):
    """Event AccountOverrideSet."""
    accountId: str
    allowance: int
    createdAt: str
    featureKey: str
    overrideId: str
    overrideType: str
    tenantId: str


class EntitlementChecked(BaseModel):
    """Event EntitlementChecked."""
    accountId: str
    allowed: bool
    decisionId: str
    evaluatedAt: str
    featureKey: str
    reason: str
    requestedQuantity: int
    subscriptionId: str
    tenantId: str


class EntitlementGranted(BaseModel):
    """Event EntitlementGranted."""
    allowance: int
    effectiveAt: str
    enforcementMode: str
    entitlementId: str
    featureKey: str
    planId: str
    tenantId: str
    unit: str


class SubscriptionStarted(BaseModel):
    """Event SubscriptionStarted."""
    accountId: str
    planId: str
    seatLimit: int
    startedAt: str
    subscriptionId: str
    tenantId: str


class UsageRecorded(BaseModel):
    """Event UsageRecorded."""
    accountId: str
    featureKey: str
    quantity: int
    recordedAt: str
    subscriptionId: str
    tenantId: str
    usageId: str
