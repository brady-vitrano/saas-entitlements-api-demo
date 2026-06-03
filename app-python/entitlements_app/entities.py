"""Pydantic models for spec entities."""

from pydantic import BaseModel



class AccountOverride(BaseModel):
    """Account-specific entitlement override that can raise, lower, grant, or deny access."""
    tenantId: str
    overrideId: str
    accountId: str
    featureKey: str
    overrideType: str
    allowance: int
    reason: str
    expiresAt: str | None = None
    createdAt: str


class AuditEvent(BaseModel):
    """Immutable audit event for entitlement decisions and subscription changes."""
    tenantId: str
    auditEventId: str
    accountId: str
    actorId: str
    eventType: str
    subjectId: str
    message: str
    occurredAt: str


class Entitlement(BaseModel):
    """Feature allowance granted by a subscription plan."""
    tenantId: str
    entitlementId: str
    planId: str
    featureKey: str
    allowance: int
    unit: str
    enforcementMode: str
    effectiveAt: str


class Feature(BaseModel):
    """Product capability that can be granted through a plan or override."""
    tenantId: str
    featureKey: str
    displayName: str
    valueType: str
    defaultUnit: str
    active: bool


class Plan(BaseModel):
    """Commercial subscription plan offered to accounts."""
    tenantId: str
    planId: str
    planCode: str
    displayName: str
    billingInterval: str
    status: str
    createdAt: str


class PolicyCheckResult(BaseModel):
    """Authorization and entitlement decision returned to product code."""
    tenantId: str
    decisionId: str
    accountId: str
    subscriptionId: str
    featureKey: str
    requestedQuantity: int
    allowed: bool
    reason: str
    evaluatedAt: str


class Subscription(BaseModel):
    """Account subscription to a commercial plan."""
    tenantId: str
    subscriptionId: str
    accountId: str
    planId: str
    status: str
    seatLimit: int
    currentSeats: int
    startedAt: str
    renewsAt: str


class UsageMeter(BaseModel):
    """Metered usage rollup for one subscription and feature."""
    tenantId: str
    usageId: str
    subscriptionId: str
    accountId: str
    featureKey: str
    quantity: int
    periodStart: str
    periodEnd: str
    recordedAt: str
