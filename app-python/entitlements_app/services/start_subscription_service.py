"""Start a subscription for an account on a plan with a seat limit."""

from decimal import Decimal
import re

from fastapi import HTTPException, status

from entitlements_app.requests.start_subscription_request import StartSubscriptionRequest
from entitlements_app.entities import Subscription
from entitlements_app.ports import EntitlementsRepository


class StartSubscriptionService:
    """Service handling Command `StartSubscription`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: StartSubscriptionRequest) -> Subscription:
        # spec-driven body for Command StartSubscription.
        # intent: Start a subscription for an account on a plan with a seat limit.
        # requires: EntitlementsRepository
        # emits events: SubscriptionStarted
        # return: Subscription (entity); construct from request inputs and persist via EntitlementsRepository.saveSubscription.
        value = Subscription(tenantId=request.tenantId, subscriptionId=request.subscriptionId, accountId=request.accountId, planId=request.planId, status=request.status, seatLimit=request.seatLimit, currentSeats=request.currentSeats, startedAt=request.startedAt, renewsAt=request.renewsAt)
        return self.entitlements_repository.saveSubscription(value)
