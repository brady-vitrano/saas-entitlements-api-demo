"""List account-specific entitlement overrides."""

from decimal import Decimal

from entitlements_app.requests.list_account_overrides_query_request import ListAccountOverridesQueryRequest
from entitlements_app.entities import AccountOverride
from entitlements_app.ports import EntitlementsRepository


class ListAccountOverridesQueryService:
    """Service handling Query `ListAccountOverrides`."""

    def __init__(self, entitlements_repository: EntitlementsRepository) -> None:
        self.entitlements_repository: EntitlementsRepository = entitlements_repository

    def handle(self, request: ListAccountOverridesQueryRequest) -> list[AccountOverride]:
        # spec-driven body for Query ListAccountOverrides.
        # intent: List account-specific entitlement overrides.
        # reads: EntitlementsRepository.listOverridesByAccount via `overrides`.
        return self.entitlements_repository.listOverridesByAccount(request.accountId)
