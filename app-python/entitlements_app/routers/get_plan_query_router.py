"""FastAPI router for query `GetPlan`."""


from fastapi import APIRouter, Depends, HTTPException, status

from entitlements_app.requests.get_plan_query_request import GetPlanQueryRequest
from entitlements_app.services.get_plan_query_service import GetPlanQueryService
from entitlements_app.ports import EntitlementsRepository
from entitlements_app.adapters import get_entitlements_repository
from entitlements_app.auth import AuthenticatedPrincipal, require_access

router = APIRouter()



_AUTHORIZATION_STAGED_MODE = False
_AUTHORIZATION_POLICY_ID = "TenantBoundaryPolicy"
_AUTHORIZATION_RULE_ID = "tenant-boundary"
_AUTHORIZATION_POLICY_IDS = ["TenantBoundaryPolicy"]
_AUTHORIZATION_RULE_IDS = ["tenant-boundary"]
_AUTHORIZATION_TARGET = "queries"
_AUTHORIZATION_OPERATION = "GetPlan"


def _authorization_failure(requirement: str, detail: str) -> None:
    if _AUTHORIZATION_STAGED_MODE:
        print("authorization.policy.decision " + _authorization_receipt_json(
            {
                "guardKind": requirement,
                "requirement": requirement,
                "allowed": False,
                "detail": detail,
            }
        ))
        return
    raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=detail)


def _authorization_receipt_json(receipt: dict[str, object]) -> str:
    import json

    normalized = {
        "schema": "policy.authorization.decision.v1",
        "policyId": _AUTHORIZATION_POLICY_ID,
        "ruleId": _AUTHORIZATION_RULE_ID,
        "policyIds": _AUTHORIZATION_POLICY_IDS,
        "ruleIds": _AUTHORIZATION_RULE_IDS,
        "target": _AUTHORIZATION_TARGET,
        "operation": _AUTHORIZATION_OPERATION,
    }
    normalized.update(receipt)
    return json.dumps(
        normalized,
        ensure_ascii=False,
        separators=(",", ":"),
        sort_keys=True,
    )


def _authorization_subject(principal: AuthenticatedPrincipal, source: str) -> str:
    if source in {"authenticated_principal_name", "authenticated_principal_subject", "principalId"}:
        return principal.subject
    if source in {"authenticated_tenant_id", "tenantId"}:
        return principal.tenant_id or ""
    if source.startswith("jwt_claim:"):
        return str((principal.claims or {}).get(source[len("jwt_claim:"):], ""))
    return ""


def _claim_matches(
    principal: AuthenticatedPrincipal, claim: str, expected: object, check: str
) -> bool:
    raw = (principal.claims or {}).get(claim)
    expected_value = "" if expected is None else str(expected)
    if check == "equals_input":
        return raw is not None and str(raw) == expected_value
    if isinstance(raw, list):
        return expected_value in {str(item) for item in raw}
    if isinstance(raw, str):
        return expected_value == raw or expected_value in raw.replace(",", " ").split()
    return False


def _authorize_pdp_decision(
    provider: str,
    decision_url_env: str,
    subject: str,
    resource: str,
    resource_id: object,
    action: str,
    fail_mode: str,
    reason_field: str,
    reason_required: bool,
    audit_event: str,
    policy_bundle_id: str,
    policy_bundle_version_env: str,
) -> bool:
    import os

    policy_bundle_version = (
        os.environ.get(policy_bundle_version_env, "") if policy_bundle_version_env else ""
    )
    if provider.lower() == "opa":
        decision = _opa_data_api_decide(
            decision_url_env,
            subject,
            resource,
            resource_id,
            action,
            reason_field,
            reason_required,
            policy_bundle_id,
            policy_bundle_version,
        )
    elif provider.lower() == "cedar":
        decision = _cedar_json_authorize_decide(
            decision_url_env,
            subject,
            resource,
            resource_id,
            action,
            reason_field,
            reason_required,
            policy_bundle_id,
            policy_bundle_version,
        )
    elif provider.lower() in {"xacml", "soap-xacml"}:
        decision = _soap_xacml_decide(
            decision_url_env,
            subject,
            resource,
            resource_id,
            action,
            reason_field,
            reason_required,
            policy_bundle_id,
            policy_bundle_version,
        )
    elif provider.lower() == "custom-entitlement":
        decision = _custom_entitlement_decide(
            decision_url_env,
            subject,
            resource,
            resource_id,
            action,
            reason_field,
            reason_required,
            policy_bundle_id,
            policy_bundle_version,
        )
    else:
        decision = _deny(
            "unsupported_pdp_provider", policy_bundle_id, policy_bundle_version
        )
    print("authorization.policy.decision " + _authorization_receipt_json(
        {
            "guardKind": "pdpDecision",
            "provider": provider,
            "event": audit_event,
            "subject": subject,
            "resource": resource,
            "resourceId": "" if resource_id is None else str(resource_id),
            "action": action,
            "failMode": fail_mode,
            "allowed": decision["allowed"],
            "reason": decision["reason"],
            "policyBundle": decision["policy_bundle"],
            "policyBundleVersion": decision["policy_bundle_version"],
        }
    ))
    if not decision["allowed"] and fail_mode in {"audit_only", "shadow"}:
        return True
    return bool(decision["allowed"])


# PDP adapter protocol: opa-data-api
def _opa_data_api_decide(
    decision_url_env: str,
    subject: str,
    resource: str,
    resource_id: object,
    action: str,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    import os

    decision_url = os.environ.get(decision_url_env, "").strip()
    if decision_url == "" or resource_id is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    body = _post_json_decision(
        decision_url,
        {
            "input": {
                "subject": subject,
                "resource": {"type": resource, "id": str(resource_id)},
                "action": action,
            }
        },
    )
    return _decision_from_opa_body(
        body,
        reason_field,
        reason_required,
        policy_bundle,
        policy_bundle_version,
    )


# PDP adapter protocol: cedar-json-authorize
def _cedar_json_authorize_decide(
    decision_url_env: str,
    subject: str,
    resource: str,
    resource_id: object,
    action: str,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    import os

    decision_url = os.environ.get(decision_url_env, "").strip()
    if decision_url == "" or resource_id is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    body = _post_json_decision(
        decision_url,
        {
            "principal": {"entityType": "User", "entityId": subject},
            "action": {"actionType": resource, "actionId": action},
            "resource": {"entityType": resource, "entityId": str(resource_id)},
            "context": {"policyBundleId": policy_bundle},
        },
    )
    return _decision_from_cedar_body(
        body,
        reason_field,
        reason_required,
        policy_bundle,
        policy_bundle_version,
    )


# PDP adapter protocol: soap-xacml
# PDP adapter security metadata envs: SOAP_XACML_WSDL_URL SOAP_XACML_MTLS_CERT_REF SOAP_XACML_XML_SIGNING_KEY_REF SOAP_XACML_TIMEOUT_MS
def _soap_xacml_decide(
    decision_url_env: str,
    subject: str,
    resource: str,
    resource_id: object,
    action: str,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    import os

    decision_url = os.environ.get(decision_url_env, "").strip()
    if decision_url == "" or resource_id is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    wsdl_url = os.environ.get("SOAP_XACML_WSDL_URL", "")
    mtls_cert_ref = os.environ.get("SOAP_XACML_MTLS_CERT_REF", "")
    xml_signing_key_ref = os.environ.get("SOAP_XACML_XML_SIGNING_KEY_REF", "")
    envelope = (
        '<?xml version="1.0" encoding="UTF-8"?>'
        '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" '
        'xmlns:xacml="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17">'
        '<soapenv:Header>'
        f'<policy:PolicyBundle xmlns:policy="https://example.com/authz" '
        f'id="{_xml_escape(policy_bundle)}" '
        f'version="{_xml_escape(policy_bundle_version)}" '
        f'wsdl="{_xml_escape(wsdl_url)}" '
        f'mtlsCertRef="{_xml_escape(mtls_cert_ref)}" '
        f'xmlSigningKeyRef="{_xml_escape(xml_signing_key_ref)}"/>'
        '</soapenv:Header><soapenv:Body><xacml:Request ReturnPolicyIdList="true">'
        '<xacml:Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject">'
        '<xacml:Attribute AttributeId="subject"><xacml:AttributeValue>'
        f'{_xml_escape(subject)}'
        '</xacml:AttributeValue></xacml:Attribute></xacml:Attributes>'
        '<xacml:Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource">'
        '<xacml:Attribute AttributeId="resource.type"><xacml:AttributeValue>'
        f'{_xml_escape(resource)}'
        '</xacml:AttributeValue></xacml:Attribute>'
        '<xacml:Attribute AttributeId="resource.id"><xacml:AttributeValue>'
        f'{_xml_escape(str(resource_id))}'
        '</xacml:AttributeValue></xacml:Attribute></xacml:Attributes>'
        '<xacml:Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">'
        '<xacml:Attribute AttributeId="action"><xacml:AttributeValue>'
        f'{_xml_escape(action)}'
        '</xacml:AttributeValue></xacml:Attribute></xacml:Attributes>'
        '</xacml:Request></soapenv:Body></soapenv:Envelope>'
    )
    body = _post_text_decision(decision_url, envelope, "text/xml")
    return _decision_from_xacml_body(
        body,
        reason_field,
        reason_required,
        policy_bundle,
        policy_bundle_version,
    )


# PDP adapter protocol: custom-json-entitlement
def _custom_entitlement_decide(
    decision_url_env: str,
    subject: str,
    resource: str,
    resource_id: object,
    action: str,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    import os

    decision_url = os.environ.get(decision_url_env, "").strip()
    if decision_url == "" or resource_id is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    body = _post_json_decision(
        decision_url,
        {
            "subject": subject,
            "resourceType": resource,
            "resourceId": str(resource_id),
            "action": action,
            "policyBundleId": policy_bundle,
        },
    )
    return _decision_from_custom_entitlement_body(
        body,
        reason_field,
        reason_required,
        policy_bundle,
        policy_bundle_version,
    )


def _post_json_decision(
    decision_url: str, payload: dict[str, object]
) -> dict[str, object] | None:
    import json
    import urllib.error
    import urllib.request

    request = urllib.request.Request(
        decision_url,
        data=json.dumps(payload).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=_pdp_timeout_seconds()) as response:
            raw = response.read().decode("utf-8")
    except (OSError, urllib.error.URLError):
        return None
    try:
        return json.loads(raw or "{}")
    except ValueError:
        return {}


def _post_text_decision(decision_url: str, payload: str, content_type: str) -> str | None:
    import urllib.error
    import urllib.request

    request = urllib.request.Request(
        decision_url,
        data=payload.encode("utf-8"),
        headers={"Content-Type": content_type},
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=_pdp_timeout_seconds()) as response:
            return response.read().decode("utf-8")
    except (OSError, UnicodeDecodeError, urllib.error.URLError):
        return None


def _pdp_timeout_seconds() -> float:
    import os

    for env_name in (
        "PDP_AUTHZ_TIMEOUT_MS",
        "SOAP_XACML_TIMEOUT_MS",
        "CUSTOM_ENTITLEMENT_TIMEOUT_MS",
    ):
        raw = os.environ.get(env_name, "").strip()
        if raw == "":
            continue
        try:
            return max(0.1, float(raw) / 1000.0)
        except ValueError:
            return 2.0
    return 2.0


def _decision_from_opa_body(
    body: dict[str, object] | None,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    if body is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    if not body or "result" not in body:
        return _deny("invalid_decision_response", policy_bundle, policy_bundle_version)
    result = body.get("result")
    if isinstance(result, bool):
        return _pdp_result(
            result,
            "allow" if result else "deny",
            policy_bundle,
            policy_bundle_version,
        )
    if isinstance(result, dict):
        allowed = result.get("allow") is True
        reason = _string_value(result.get(reason_field)) or _string_value(
            result.get("reason")
        )
        if reason_required and reason == "":
            return _deny("missing_decision_reason", policy_bundle, policy_bundle_version)
        if reason == "":
            reason = "allow" if allowed else "deny"
        return _pdp_result(allowed, reason, policy_bundle, policy_bundle_version)
    return _deny("invalid_decision_response", policy_bundle, policy_bundle_version)


def _decision_from_cedar_body(
    body: dict[str, object] | None,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    if body is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    decision = _string_value(body.get("decision")).lower()
    if decision == "":
        return _deny("invalid_decision_response", policy_bundle, policy_bundle_version)
    allowed = decision == "allow"
    reason = _string_value(body.get("reason"))
    diagnostics = body.get("diagnostics")
    if reason == "" and isinstance(diagnostics, dict):
        reason = _string_value(diagnostics.get(reason_field)) or _string_value(
            diagnostics.get("reason")
        )
    if reason_required and reason == "":
        return _deny("missing_decision_reason", policy_bundle, policy_bundle_version)
    if reason == "":
        reason = "allow" if allowed else "deny"
    return _pdp_result(allowed, reason, policy_bundle, policy_bundle_version)


def _decision_from_xacml_body(
    body: str | None,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    if body is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    if body.strip() == "":
        return _deny("invalid_decision_response", policy_bundle, policy_bundle_version)
    decision = _xml_element_text(body, "Decision")
    if decision == "":
        return _deny("invalid_decision_response", policy_bundle, policy_bundle_version)
    allowed = decision.lower() == "permit"
    reason = (
        _xml_element_text(body, reason_field)
        or _xml_element_text(body, "StatusMessage")
        or _xml_element_text(body, "Reason")
    )
    if reason_required and reason == "":
        return _deny("missing_decision_reason", policy_bundle, policy_bundle_version)
    if reason == "":
        reason = "permit" if allowed else "deny"
    return _pdp_result(allowed, reason, policy_bundle, policy_bundle_version)


def _decision_from_custom_entitlement_body(
    body: dict[str, object] | None,
    reason_field: str,
    reason_required: bool,
    policy_bundle: str,
    policy_bundle_version: str,
) -> dict[str, object]:
    if body is None:
        return _deny("pdp_unavailable", policy_bundle, policy_bundle_version)
    if not body:
        return _deny("invalid_decision_response", policy_bundle, policy_bundle_version)
    raw_allow = body.get("allow")
    raw_decision = _string_value(body.get("decision")).lower()
    if not isinstance(raw_allow, bool) and raw_decision == "":
        return _deny("invalid_decision_response", policy_bundle, policy_bundle_version)
    allowed = (
        raw_allow
        if isinstance(raw_allow, bool)
        else raw_decision in {"allow", "permit"}
    )
    reason = (
        _string_value(body.get(reason_field))
        or _string_value(body.get("reason"))
        or _string_value(body.get("statusMessage"))
    )
    if reason_required and reason == "":
        return _deny("missing_decision_reason", policy_bundle, policy_bundle_version)
    if reason == "":
        reason = "allow" if allowed else "deny"
    return _pdp_result(bool(allowed), reason, policy_bundle, policy_bundle_version)


def _deny(
    reason: str, policy_bundle: str, policy_bundle_version: str
) -> dict[str, object]:
    return _pdp_result(False, reason, policy_bundle, policy_bundle_version)


def _pdp_result(
    allowed: bool, reason: str, policy_bundle: str, policy_bundle_version: str
) -> dict[str, object]:
    return {
        "allowed": allowed,
        "reason": reason,
        "policy_bundle": policy_bundle,
        "policy_bundle_version": policy_bundle_version,
    }


def _string_value(value: object) -> str:
    return "" if value is None else str(value)


def _xml_escape(value: object) -> str:
    import html

    return html.escape("" if value is None else str(value), quote=True)


def _xml_element_text(body: str, local_name: str) -> str:
    import xml.etree.ElementTree as ET

    if local_name == "":
        return ""
    try:
        root = ET.fromstring(body)
    except ET.ParseError:
        return ""
    for element in root.iter():
        tag = element.tag.rsplit("}", 1)[-1].rsplit(":", 1)[-1]
        if tag == local_name:
            return "".join(element.itertext()).strip()
    return ""



@router.get("/api/plans/{planId}")
def handle(planId: str, tenantId: str, entitlements_repository: EntitlementsRepository = Depends(get_entitlements_repository), principal: AuthenticatedPrincipal = Depends(require_access([], ["entitlements:access"]))):
    request = GetPlanQueryRequest(planId=planId, tenantId=tenantId)
    service = GetPlanQueryService(entitlements_repository=entitlements_repository)
    # authorization: authorizationGuard.scopeCheck scope=entitlements:access
    if "entitlements:access" not in principal.scopes:
        _authorization_failure("scopeCheck", "scope entitlements:access required")
    # authorization: authorizationGuard.tenantMatch input=tenantId source=authenticated_tenant_id check=match_input
    if str(tenantId) != _authorization_subject(principal, "authenticated_tenant_id"):
        _authorization_failure("tenantMatch", "tenant boundary required")
    return service.handle(request)
