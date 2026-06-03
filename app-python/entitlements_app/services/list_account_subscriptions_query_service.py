"""List subscriptions for an account."""

from decimal import Decimal

from entitlements_app.requests.list_account_subscriptions_query_request import ListAccountSubscriptionsQueryRequest
from entitlements_app.entities import Subscription
from entitlements_app.ports import EntitlementsRepository


class ListAccountSubscriptionsQueryService:
    """Service handling Query `ListAccountSubscriptions`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: ListAccountSubscriptionsQueryRequest) -> list[Subscription]:
        # spec-driven body for Query ListAccountSubscriptions.
        # intent: List subscriptions for an account.
        # reads: EntitlementsRepository.listSubscriptionsByAccount via `subscriptions`.
        return self.entitlements_repository.listSubscriptionsByAccount(request.accountId)
