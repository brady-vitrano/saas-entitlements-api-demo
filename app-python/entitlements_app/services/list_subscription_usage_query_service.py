"""List usage records for one subscription."""

from decimal import Decimal

from entitlements_app.requests.list_subscription_usage_query_request import ListSubscriptionUsageQueryRequest
from entitlements_app.entities import UsageMeter
from entitlements_app.ports import EntitlementsRepository


class ListSubscriptionUsageQueryService:
    """Service handling Query `ListSubscriptionUsage`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: ListSubscriptionUsageQueryRequest) -> list[UsageMeter]:
        # spec-driven body for Query ListSubscriptionUsage.
        # intent: List usage records for one subscription.
        # reads: EntitlementsRepository.listUsageBySubscription via `usageRecords`.
        return self.entitlements_repository.listUsageBySubscription(request.subscriptionId)
