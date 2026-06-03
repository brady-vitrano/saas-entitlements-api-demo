"""HTTP Basic auth helper.

Password = "secret". The table includes legacy role-named users plus same-tenant
role users for runtime proofs. This remains test-shaped; production deployments
would use OAuth2 resource-server config with real OIDC issuer wiring.
"""

from dataclasses import dataclass
from typing import Callable

from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBasic, HTTPBasicCredentials


_security = HTTPBasic()

_USERS: dict[str, list[str]] = {
    "account-operator": ["account-operator"],  # password = "secret"
    "account-operator_tenant_a": ["account-operator"],  # password = "secret"
    "entitlement-admin": ["entitlement-admin"],  # password = "secret"
    "entitlement-admin_tenant_a": ["entitlement-admin"],  # password = "secret"
}

_USER_SCOPES: dict[str, list[str]] = {
    "account-operator": ["entitlements:access", "entitlements:write"],
    "account-operator_tenant_a": ["entitlements:access", "entitlements:write"],
    "entitlement-admin": ["entitlements:access", "entitlements:write"],
    "entitlement-admin_tenant_a": ["entitlements:access", "entitlements:write"],
}

_USER_SUBJECTS: dict[str, str] = {
    "account-operator": "account-operator",
    "account-operator_tenant_a": "account-operator",
    "entitlement-admin": "entitlement-admin",
    "entitlement-admin_tenant_a": "entitlement-admin",
}

_USER_TENANTS: dict[str, str] = {
    "account-operator": "account-operator",
    "account-operator_tenant_a": "tenant-a",
    "entitlement-admin": "entitlement-admin",
    "entitlement-admin_tenant_a": "tenant-a",
}

_USER_ALLOWED_TENANTS: dict[str, list[str]] = {
    "account-operator": ["account-operator"],
    "account-operator_tenant_a": ["tenant-a"],
    "entitlement-admin": ["entitlement-admin"],
    "entitlement-admin_tenant_a": ["tenant-a"],
}

_USER_ALLOWED_CLAIMS: dict[str, dict[str, list[str]]] = {
    "account-operator": {"allowed_account_ids": ["account-operator"]},
    "account-operator_tenant_a": {"allowed_account_ids": ["tenant-a"]},
    "entitlement-admin": {"allowed_account_ids": ["entitlement-admin"]},
    "entitlement-admin_tenant_a": {"allowed_account_ids": ["tenant-a"]},
}

_PASSWORD = "secret"


@dataclass(frozen=True)
class AuthenticatedPrincipal:
    subject: str
    roles: list[str]
    scopes: list[str]
    tenant_id: str | None = None
    claims: dict[str, object] | None = None


def _authenticate(credentials: HTTPBasicCredentials = Depends(_security)) -> AuthenticatedPrincipal:
    user_roles = _USERS.get(credentials.username)
    if (user_roles is None and _USERS) or credentials.password != _PASSWORD:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="invalid credentials",
            headers={"WWW-Authenticate": "Basic"},
        )
    roles = list(user_roles or [credentials.username])
    scopes = list(_USER_SCOPES.get(credentials.username, []))
    subject = _USER_SUBJECTS.get(credentials.username, credentials.username)
    tenant_id = _USER_TENANTS.get(credentials.username, credentials.username)
    allowed_tenant_ids = list(_USER_ALLOWED_TENANTS.get(credentials.username, [tenant_id]))
    allowed_claims = dict(_USER_ALLOWED_CLAIMS.get(credentials.username, {}))
    return AuthenticatedPrincipal(
        subject=subject,
        roles=roles,
        scopes=scopes,
        tenant_id=tenant_id,
        claims={
            **allowed_claims,
            "sub": subject,
            "tenant_id": tenant_id,
            "roles": roles,
            "allowed_tenant_ids": allowed_tenant_ids,
            "scope": " ".join(scopes),
            "scp": scopes,
        },
    )


def authenticated_principal(
    actual: AuthenticatedPrincipal = Depends(_authenticate),
) -> AuthenticatedPrincipal:
    return actual


def require_role(role: str) -> Callable[[AuthenticatedPrincipal], AuthenticatedPrincipal]:
    def dep(actual: AuthenticatedPrincipal = Depends(_authenticate)) -> AuthenticatedPrincipal:
        if role not in actual.roles:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"role {role!r} required",
            )
        return actual

    return dep


def require_any_role(roles: list[str]) -> Callable[[AuthenticatedPrincipal], AuthenticatedPrincipal]:
    def dep(actual: AuthenticatedPrincipal = Depends(_authenticate)) -> AuthenticatedPrincipal:
        if not any(role in actual.roles for role in roles):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"one of {roles!r} required",
            )
        return actual

    return dep


def require_access(
    roles: list[str], scopes: list[str]
) -> Callable[[AuthenticatedPrincipal], AuthenticatedPrincipal]:
    def dep(actual: AuthenticatedPrincipal = Depends(_authenticate)) -> AuthenticatedPrincipal:
        role_ok = not roles or any(role in actual.roles for role in roles)
        scope_ok = not scopes or any(scope in actual.scopes for scope in scopes)
        if not (role_ok and scope_ok):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"roles {roles!r} and scopes {scopes!r} required",
            )
        return actual

    return dep
