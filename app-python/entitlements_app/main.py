"""FastAPI entry point."""

from fastapi import FastAPI
from entitlements_app.routers.add_feature_to_plan_router import router as add_feature_to_plan_router
from entitlements_app.routers.append_audit_event_router import router as append_audit_event_router
from entitlements_app.routers.check_feature_access_router import router as check_feature_access_router
from entitlements_app.routers.create_plan_router import router as create_plan_router
from entitlements_app.routers.record_usage_router import router as record_usage_router
from entitlements_app.routers.register_feature_router import router as register_feature_router
from entitlements_app.routers.set_account_override_router import router as set_account_override_router
from entitlements_app.routers.start_subscription_router import router as start_subscription_router
from entitlements_app.routers.get_feature_query_router import router as get_feature_query_router
from entitlements_app.routers.get_plan_query_router import router as get_plan_query_router
from entitlements_app.routers.list_account_audit_events_query_router import router as list_account_audit_events_query_router
from entitlements_app.routers.list_account_overrides_query_router import router as list_account_overrides_query_router
from entitlements_app.routers.list_account_subscriptions_query_router import router as list_account_subscriptions_query_router
from entitlements_app.routers.list_plan_entitlements_query_router import router as list_plan_entitlements_query_router
from entitlements_app.routers.list_subscription_usage_query_router import router as list_subscription_usage_query_router



def create_app() -> FastAPI:
    """Application factory: builds the FastAPI app and mounts every operation router.

    Production deployments wire real persistence (sqlite/postgres adapters) + real
    OAuth2 bearer auth; this scaffold uses the in-memory
    adapters (`entitlements_app.adapters`) and HTTP Basic auth (`entitlements_app.auth`) when authz roles
    are declared in the spec.
    """
    app = FastAPI(title="entitlements_app", version="0.1.0")

    @app.get("/healthz")
    def healthz() -> dict[str, str]:
        return {"status": "UP"}

    app.include_router(add_feature_to_plan_router)
    app.include_router(append_audit_event_router)
    app.include_router(check_feature_access_router)
    app.include_router(create_plan_router)
    app.include_router(record_usage_router)
    app.include_router(register_feature_router)
    app.include_router(set_account_override_router)
    app.include_router(start_subscription_router)
    app.include_router(get_feature_query_router)
    app.include_router(get_plan_query_router)
    app.include_router(list_account_audit_events_query_router)
    app.include_router(list_account_overrides_query_router)
    app.include_router(list_account_subscriptions_query_router)
    app.include_router(list_plan_entitlements_query_router)
    app.include_router(list_subscription_usage_query_router)
    return app


app = create_app()