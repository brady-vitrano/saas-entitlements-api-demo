"""In-memory adapter implementations of every spec port.

Production deployments swap these for real adapters via the persistence capability
packs (sqlite, postgres, http-client). The in-memory shape is enough to verify
spec-driven business logic at runtime today.

Each port gets a `get_<port_snake>` factory cached with `lru_cache(maxsize=1)` so
the FastAPI Depends() machinery hands routers a process-wide singleton -- same
shape as Spring's `@Autowired` of an InMemoryAdapter bean (state visible across
requests, identity preserved across calls).
"""

from functools import lru_cache

from entitlements_app.ports import EntitlementsRepository
from entitlements_app.entities import AccountOverride, AuditEvent, Entitlement, Feature, Plan, PolicyCheckResult, Subscription, UsageMeter




class EntitlementsRepositoryInMemoryAdapter(EntitlementsRepository):
    """In-memory adapter for EntitlementsRepository.

    Operations dispatched by name prefix:
      - save / store / add / create -> dict[id] = entity
      - find / get / lookup          -> identity lookup or contract-planned value scan
      - list / all                   -> contract-planned value scan
    """

    def __init__(self) -> None:
        self._account_override: dict[str, AccountOverride] = {}
        self._audit_event: dict[str, AuditEvent] = {}
        self._entitlement: dict[str, Entitlement] = {}
        self._feature: dict[str, Feature] = {}
        self._plan: dict[str, Plan] = {}
        self._policy_check_result: dict[str, PolicyCheckResult] = {}
        self._subscription: dict[str, Subscription] = {}
        self._usage_meter: dict[str, UsageMeter] = {}


    def savePlan(self, plan: Plan) -> Plan:
        self._plan[str(plan.planId)] = plan
        return plan

    def save_plan(self, plan: Plan) -> Plan:
        return self.savePlan(plan)


    def findPlan(self, planId: str) -> Plan:
        return self._plan.get(planId)

    def find_plan(self, plan_id: str) -> Plan:
        return self.findPlan(plan_id)


    def saveFeature(self, feature: Feature) -> Feature:
        self._feature[str(feature.featureKey)] = feature
        return feature

    def save_feature(self, feature: Feature) -> Feature:
        return self.saveFeature(feature)


    def findFeature(self, featureKey: str) -> Feature:
        return self._feature.get(featureKey)

    def find_feature(self, feature_key: str) -> Feature:
        return self.findFeature(feature_key)


    def saveEntitlement(self, entitlement: Entitlement) -> Entitlement:
        self._entitlement[str(entitlement.entitlementId)] = entitlement
        return entitlement

    def save_entitlement(self, entitlement: Entitlement) -> Entitlement:
        return self.saveEntitlement(entitlement)


    def findEntitlement(self, entitlementId: str) -> Entitlement:
        return self._entitlement.get(entitlementId)

    def find_entitlement(self, entitlement_id: str) -> Entitlement:
        return self.findEntitlement(entitlement_id)


    def listEntitlementsByPlan(self, planId: str) -> list[Entitlement]:
        return [value for value in self._entitlement.values() if value.planId == planId]

    def list_entitlements_by_plan(self, plan_id: str) -> list[Entitlement]:
        return self.listEntitlementsByPlan(plan_id)


    def saveSubscription(self, subscription: Subscription) -> Subscription:
        self._subscription[str(subscription.subscriptionId)] = subscription
        return subscription

    def save_subscription(self, subscription: Subscription) -> Subscription:
        return self.saveSubscription(subscription)


    def findSubscription(self, subscriptionId: str) -> Subscription:
        return self._subscription.get(subscriptionId)

    def find_subscription(self, subscription_id: str) -> Subscription:
        return self.findSubscription(subscription_id)


    def listSubscriptionsByAccount(self, accountId: str) -> list[Subscription]:
        return [value for value in self._subscription.values() if value.accountId == accountId]

    def list_subscriptions_by_account(self, account_id: str) -> list[Subscription]:
        return self.listSubscriptionsByAccount(account_id)


    def saveUsageMeter(self, usageMeter: UsageMeter) -> UsageMeter:
        self._usage_meter[str(usageMeter.usageId)] = usageMeter
        return usageMeter

    def save_usage_meter(self, usage_meter: UsageMeter) -> UsageMeter:
        return self.saveUsageMeter(usage_meter)


    def findUsageMeter(self, usageId: str) -> UsageMeter:
        return self._usage_meter.get(usageId)

    def find_usage_meter(self, usage_id: str) -> UsageMeter:
        return self.findUsageMeter(usage_id)


    def listUsageBySubscription(self, subscriptionId: str) -> list[UsageMeter]:
        return [value for value in self._usage_meter.values() if value.subscriptionId == subscriptionId]

    def list_usage_by_subscription(self, subscription_id: str) -> list[UsageMeter]:
        return self.listUsageBySubscription(subscription_id)


    def saveAccountOverride(self, accountOverride: AccountOverride) -> AccountOverride:
        self._account_override[str(accountOverride.overrideId)] = accountOverride
        return accountOverride

    def save_account_override(self, account_override: AccountOverride) -> AccountOverride:
        return self.saveAccountOverride(account_override)


    def findAccountOverride(self, overrideId: str) -> AccountOverride:
        return self._account_override.get(overrideId)

    def find_account_override(self, override_id: str) -> AccountOverride:
        return self.findAccountOverride(override_id)


    def listOverridesByAccount(self, accountId: str) -> list[AccountOverride]:
        return [value for value in self._account_override.values() if value.accountId == accountId]

    def list_overrides_by_account(self, account_id: str) -> list[AccountOverride]:
        return self.listOverridesByAccount(account_id)


    def savePolicyCheckResult(self, policyCheckResult: PolicyCheckResult) -> PolicyCheckResult:
        self._policy_check_result[str(policyCheckResult.decisionId)] = policyCheckResult
        return policyCheckResult

    def save_policy_check_result(self, policy_check_result: PolicyCheckResult) -> PolicyCheckResult:
        return self.savePolicyCheckResult(policy_check_result)


    def findPolicyCheckResult(self, decisionId: str) -> PolicyCheckResult:
        return self._policy_check_result.get(decisionId)

    def find_policy_check_result(self, decision_id: str) -> PolicyCheckResult:
        return self.findPolicyCheckResult(decision_id)


    def saveAuditEvent(self, auditEvent: AuditEvent) -> AuditEvent:
        self._audit_event[str(auditEvent.auditEventId)] = auditEvent
        return auditEvent

    def save_audit_event(self, audit_event: AuditEvent) -> AuditEvent:
        return self.saveAuditEvent(audit_event)


    def listAuditEventsByAccount(self, accountId: str) -> list[AuditEvent]:
        return [value for value in self._audit_event.values() if value.accountId == accountId]

    def list_audit_events_by_account(self, account_id: str) -> list[AuditEvent]:
        return self.listAuditEventsByAccount(account_id)




@lru_cache(maxsize=1)
def get_entitlements_repository() -> EntitlementsRepository:
    return EntitlementsRepositoryInMemoryAdapter()


def _policy_post_json(url: str, payload: dict[str, object], token: str | None = None) -> dict[str, object]:
    import json
    import urllib.error
    import urllib.request

    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    request = urllib.request.Request(
        url,
        data=json.dumps(payload).encode("utf-8"),
        headers=headers,
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=2.0) as response:
            return json.loads(response.read().decode("utf-8") or "{}")
    except (OSError, ValueError, urllib.error.URLError):
        return {}


# relationship adapter protocol: openfga-check
def _openfga_check(principal: str, resource_id: str, resource: str, relation: str) -> bool:
    import os

    api_url = os.getenv("OPENFGA_API_URL", "").rstrip("/")
    store_id = os.getenv("OPENFGA_STORE_ID", "")
    model_id = os.getenv("OPENFGA_AUTHORIZATION_MODEL_ID", "")
    if not api_url or not store_id or not principal or not resource_id:
        return False
    payload: dict[str, object] = {
        "tuple_key": {
            "user": f"user:{principal}",
            "relation": relation,
            "object": f"{resource}:{resource_id}",
        }
    }
    if model_id:
        payload["authorization_model_id"] = model_id
    body = _policy_post_json(f"{api_url}/stores/{store_id}/check", payload)
    return body.get("allowed") is True


# relationship adapter protocol: spicedb-check
def _spicedb_check(principal: str, resource_id: str, resource: str, relation: str) -> bool:
    import os

    api_url = os.getenv("SPICEDB_API_URL", "").rstrip("/")
    token = os.getenv("SPICEDB_TOKEN", "")
    if not api_url or not token or not principal or not resource_id:
        return False
    body = _policy_post_json(
        f"{api_url}/v1/permissions/check",
        {
            "resource": {"objectType": resource, "objectId": resource_id},
            "permission": relation,
            "subject": {"object": {"objectType": "user", "objectId": principal}},
        },
        token,
    )
    permissionship = str(body.get("permissionship") or "")
    return permissionship in {"HAS_PERMISSION", "PERMISSIONSHIP_HAS_PERMISSION"}