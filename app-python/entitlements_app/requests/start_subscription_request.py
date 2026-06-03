"""Pydantic request model for command `StartSubscription`."""

from pydantic import BaseModel


class StartSubscriptionRequest(BaseModel):
    accountId: str
    currentSeats: int
    planId: str
    renewsAt: str
    seatLimit: int
    startedAt: str
    status: str
    subscriptionId: str
    tenantId: str

    @property
    def account_id(self) -> str:
        return self.accountId

    @property
    def current_seats(self) -> int:
        return self.currentSeats

    @property
    def plan_id(self) -> str:
        return self.planId

    @property
    def renews_at(self) -> str:
        return self.renewsAt

    @property
    def seat_limit(self) -> int:
        return self.seatLimit

    @property
    def started_at(self) -> str:
        return self.startedAt

    @property
    def subscription_id(self) -> str:
        return self.subscriptionId

    @property
    def tenant_id(self) -> str:
        return self.tenantId
