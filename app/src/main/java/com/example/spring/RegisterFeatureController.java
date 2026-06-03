package com.example.spring;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.util.Objects;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST surface for command RegisterFeature.
 */
@RestController
public class RegisterFeatureController {

    private final RegisterFeatureService service;


    @Autowired
    public RegisterFeatureController(RegisterFeatureService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('entitlement-admin')")
    @PostMapping("/api/features")
    public ResponseEntity<Feature> handle(@Valid @RequestBody RegisterFeatureRequest request, Authentication authentication) {
        // authorization: authorizationGuard.scopeCheck scope=entitlements:access
        if (!authenticationHasScope(authentication, "entitlements:access")) {
            handleAuthorizationFailure("scopeCheck", "scope entitlements:access required");
        }
        // authorization: authorizationGuard.scopeCheck scope=entitlements:write
        if (!authenticationHasScope(authentication, "entitlements:write")) {
            handleAuthorizationFailure("scopeCheck", "scope entitlements:write required");
        }
        // authorization: authorizationGuard.tenantMatch input=tenantId source=authenticated_tenant_id check=match_input
        if (!authorizationValueEquals(request.tenantId(), authorizationSubject(authentication, "authenticated_tenant_id"))) {
            handleAuthorizationFailure("tenantMatch", "tenant boundary required");
        }
        return ResponseEntity.status(201).body(service.handle(request));
    }


    private void handleAuthorizationFailure(String requirement, String detail) {
        if (false) {
            System.out.println("authorization.policy.decision "
                    + authorizationFailureReceipt(requirement, detail));
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, detail);
    }

    private boolean authenticationHasScope(Authentication authentication, String scope) {
        if (authentication == null || scope == null || scope.isBlank()) {
            return false;
        }
        for (var authority : authentication.getAuthorities()) {
            String value = authority.getAuthority();
            if (Objects.equals(value, scope) || Objects.equals(value, "SCOPE_" + scope)) {
                return true;
            }
        }
        Object rawScope = identityClaim(authentication, "scope");
        if (authorizationContains(rawScope, scope)) {
            return true;
        }
        Object rawScp = identityClaim(authentication, "scp");
        return authorizationContains(rawScp, scope);
    }

    private String authorizationSubject(Authentication authentication, String source) {
        if (authentication == null || source == null || source.isBlank()) {
            return "";
        }
        if ("authenticated_principal_name".equals(source)) {
            return authentication.getName();
        }
        if ("authenticated_principal_subject".equals(source) || "principalId".equals(source)) {
            return authorizationString(identityClaim(authentication, "sub"));
        }
        if ("authenticated_tenant_id".equals(source) || "tenantId".equals(source)) {
            return authorizationString(identityClaim(authentication, "tenant_id"));
        }
        if (source.startsWith("jwt_claim:")) {
            return authorizationString(identityClaim(authentication, source.substring("jwt_claim:".length())));
        }
        return "";
    }

    private Object identityClaim(Authentication authentication, String claim) {
        if (authentication == null || claim == null || claim.isBlank()) {
            return null;
        }
        if ("sub".equals(claim) || "subject".equals(claim)) {
            return SecurityConfig.testSubjectFor(authentication.getName());
        }
        if ("tenant_id".equals(claim)) {
            return SecurityConfig.testTenantFor(authentication.getName());
        }
        if ("allowed_tenant_ids".equals(claim)) {
            return SecurityConfig.testAllowedTenantsFor(authentication.getName());
        }
        java.util.List<String> generatedClaim = SecurityConfig.testClaimFor(authentication.getName(), claim);
        if (generatedClaim != null) {
            return generatedClaim;
        }
        if ("roles".equals(claim) || "groups".equals(claim)) {
            return authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .filter(value -> value.startsWith("ROLE_"))
                    .map(value -> value.substring("ROLE_".length()))
                    .toList();
        }
        if ("scope".equals(claim) || "scp".equals(claim)) {
            return authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .filter(value -> value.startsWith("SCOPE_"))
                    .map(value -> value.substring("SCOPE_".length()))
                    .toList();
        }

        return null;
    }

    private boolean identityClaimMatches(
            Authentication authentication, String claim, Object expected, String check) {
        Object raw = identityClaim(authentication, claim);
        if ("equals_input".equals(check)) {
            return authorizationValueEquals(raw, expected);
        }
        return authorizationContains(raw, authorizationString(expected));
    }

    private boolean authorizationContains(Object raw, String expected) {
        if (raw == null || expected == null || expected.isBlank()) {
            return false;
        }
        if (raw instanceof java.util.Collection<?> collection) {
            return collection.stream().anyMatch(value -> Objects.equals(String.valueOf(value), expected));
        }
        String value = String.valueOf(raw);
        if (Objects.equals(value, expected)) {
            return true;
        }
        for (String part : value.split("[\\s,]+")) {
            if (Objects.equals(part, expected)) {
                return true;
            }
        }
        return false;
    }

    private boolean authorizationValueEquals(Object actual, Object expected) {
        String left = authorizationString(actual);
        String right = authorizationString(expected);
        return !left.isBlank() && Objects.equals(left, right);
    }

    private String authorizationString(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    private boolean authorizePdpDecision(
            String provider,
            String decisionUrlEnv,
            String subject,
            String resource,
            Object resourceId,
            String action,
            String failMode,
            String reasonField,
            boolean reasonRequired,
            String auditEvent,
            String policyBundleId,
            String policyBundleVersionEnv) {
        String policyBundleVersion =
                policyBundleVersionEnv == null || policyBundleVersionEnv.isBlank()
                        ? ""
                        : System.getenv(policyBundleVersionEnv);
        PdpDecision decision;
        if ("opa".equalsIgnoreCase(provider)) {
            decision =
                    decideOpaDataApi(
                            decisionUrlEnv,
                            subject,
                            resource,
                            resourceId,
                            action,
                            reasonField,
                            reasonRequired,
                            policyBundleId,
                            authorizationString(policyBundleVersion));
        } else if ("cedar".equalsIgnoreCase(provider)) {
            decision =
                    decideCedarJsonAuthorize(
                            decisionUrlEnv,
                            subject,
                            resource,
                            resourceId,
                            action,
                            reasonField,
                            reasonRequired,
                            policyBundleId,
                            authorizationString(policyBundleVersion));
        } else if ("xacml".equalsIgnoreCase(provider) || "soap-xacml".equalsIgnoreCase(provider)) {
            decision =
                    decideSoapXacml(
                            decisionUrlEnv,
                            subject,
                            resource,
                            resourceId,
                            action,
                            reasonField,
                            reasonRequired,
                            policyBundleId,
                            authorizationString(policyBundleVersion));
        } else if ("custom-entitlement".equalsIgnoreCase(provider)) {
            decision =
                    decideCustomEntitlement(
                            decisionUrlEnv,
                            subject,
                            resource,
                            resourceId,
                            action,
                            reasonField,
                            reasonRequired,
                            policyBundleId,
                            authorizationString(policyBundleVersion));
        } else {
            decision =
                    deny(
                            "unsupported_pdp_provider",
                            policyBundleId,
                            authorizationString(policyBundleVersion));
        }
        System.out.println("authorization.policy.decision "
                + authorizationDecisionReceipt(
                        provider,
                        auditEvent,
                        subject,
                        resource,
                        resourceId,
                        action,
                        failMode,
                        decision));
        if (!decision.allowed() && ("audit_only".equals(failMode) || "shadow".equals(failMode))) {
            return true;
        }
        return decision.allowed();
    }

    private String authorizationDecisionReceipt(
            String provider,
            String auditEvent,
            String subject,
            String resource,
            Object resourceId,
            String action,
            String failMode,
            PdpDecision decision) {
        return "{"
                + "\"schema\":\"authz.policy.decision.v1\","
                + "\"policyId\":\"PlanAdminPolicy\","
                + "\"ruleId\":\"plan-admin-write\","
                + "\"policyIds\":[\"PlanAdminPolicy\",\"TenantBoundaryPolicy\"],"
                + "\"ruleIds\":[\"plan-admin-write\",\"tenant-boundary\"],"
                + "\"target\":\"commands\","
                + "\"operation\":\"RegisterFeature\","
                + "\"guardKind\":\"pdpDecision\","
                + "\"provider\":\"" + jsonEscape(provider) + "\","
                + "\"event\":\"" + jsonEscape(auditEvent) + "\","
                + "\"subject\":\"" + jsonEscape(subject) + "\","
                + "\"resource\":\"" + jsonEscape(resource) + "\","
                + "\"resourceId\":\"" + jsonEscape(authorizationString(resourceId)) + "\","
                + "\"action\":\"" + jsonEscape(action) + "\","
                + "\"failMode\":\"" + jsonEscape(failMode) + "\","
                + "\"allowed\":" + decision.allowed() + ","
                + "\"reason\":\"" + jsonEscape(decision.reason()) + "\","
                + "\"policyBundle\":\"" + jsonEscape(decision.policyBundleId()) + "\","
                + "\"policyBundleVersion\":\"" + jsonEscape(decision.policyBundleVersion()) + "\""
                + "}";
    }

    private String authorizationFailureReceipt(String requirement, String detail) {
        return "{"
                + "\"schema\":\"authz.policy.decision.v1\","
                + "\"policyId\":\"PlanAdminPolicy\","
                + "\"ruleId\":\"plan-admin-write\","
                + "\"policyIds\":[\"PlanAdminPolicy\",\"TenantBoundaryPolicy\"],"
                + "\"ruleIds\":[\"plan-admin-write\",\"tenant-boundary\"],"
                + "\"target\":\"commands\","
                + "\"operation\":\"RegisterFeature\","
                + "\"guardKind\":\"" + jsonEscape(requirement) + "\","
                + "\"requirement\":\"" + jsonEscape(requirement) + "\","
                + "\"allowed\":false,"
                + "\"detail\":\"" + jsonEscape(detail) + "\""
                + "}";
    }

// PDP adapter protocol: opa-data-api
    private PdpDecision decideOpaDataApi(
            String decisionUrlEnv,
            String subject,
            String resource,
            Object resourceId,
            String action,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        String decisionUrl = System.getenv(decisionUrlEnv);
        if (decisionUrl == null || decisionUrl.isBlank() || resourceId == null) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
        String payload = "{\"input\":{\"subject\":\"" + jsonEscape(subject)
                + "\",\"resource\":{\"type\":\"" + jsonEscape(resource)
                + "\",\"id\":\"" + jsonEscape(String.valueOf(resourceId))
                + "\"},\"action\":\"" + jsonEscape(action) + "\"}}";
        try {
            return opaDecisionFromBody(
                    postPdpJson(decisionUrl, payload),
                    reasonField,
                    reasonRequired,
                    policyBundleId,
                    policyBundleVersion);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return deny("pdp_interrupted", policyBundleId, policyBundleVersion);
        } catch (RuntimeException | java.io.IOException ex) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
    }

    // PDP adapter protocol: cedar-json-authorize
    private PdpDecision decideCedarJsonAuthorize(
            String decisionUrlEnv,
            String subject,
            String resource,
            Object resourceId,
            String action,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        String decisionUrl = System.getenv(decisionUrlEnv);
        if (decisionUrl == null || decisionUrl.isBlank() || resourceId == null) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
        String payload = "{\"principal\":{\"entityType\":\"User\",\"entityId\":\""
                + jsonEscape(subject)
                + "\"},\"action\":{\"actionType\":\""
                + jsonEscape(resource)
                + "\",\"actionId\":\""
                + jsonEscape(action)
                + "\"},\"resource\":{\"entityType\":\""
                + jsonEscape(resource)
                + "\",\"entityId\":\""
                + jsonEscape(String.valueOf(resourceId))
                + "\"},\"context\":{\"policyBundleId\":\""
                + jsonEscape(policyBundleId)
                + "\"}}";
        try {
            return cedarDecisionFromBody(
                    postPdpJson(decisionUrl, payload),
                    reasonField,
                    reasonRequired,
                    policyBundleId,
                    policyBundleVersion);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return deny("pdp_interrupted", policyBundleId, policyBundleVersion);
        } catch (RuntimeException | java.io.IOException ex) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
    }

    // PDP adapter protocol: soap-xacml
    // PDP adapter security metadata envs: SOAP_XACML_WSDL_URL SOAP_XACML_MTLS_CERT_REF SOAP_XACML_XML_SIGNING_KEY_REF SOAP_XACML_TIMEOUT_MS
    private PdpDecision decideSoapXacml(
            String decisionUrlEnv,
            String subject,
            String resource,
            Object resourceId,
            String action,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        String decisionUrl = System.getenv(decisionUrlEnv);
        if (decisionUrl == null || decisionUrl.isBlank() || resourceId == null) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
        String wsdlUrl = System.getenv("SOAP_XACML_WSDL_URL");
        String mtlsCertRef = System.getenv("SOAP_XACML_MTLS_CERT_REF");
        String xmlSigningKeyRef = System.getenv("SOAP_XACML_XML_SIGNING_KEY_REF");
        String payload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:xacml=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">"
                + "<soapenv:Header>"
                + "<authz:PolicyBundle xmlns:authz=\"https://example.com/authz\" id=\""
                + xmlEscape(policyBundleId)
                + "\" version=\""
                + xmlEscape(policyBundleVersion)
                + "\" wsdl=\""
                + xmlEscape(wsdlUrl)
                + "\" mtlsCertRef=\""
                + xmlEscape(mtlsCertRef)
                + "\" xmlSigningKeyRef=\""
                + xmlEscape(xmlSigningKeyRef)
                + "\"/>"
                + "</soapenv:Header><soapenv:Body><xacml:Request ReturnPolicyIdList=\"true\">"
                + "<xacml:Attributes Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\">"
                + "<xacml:Attribute AttributeId=\"subject\"><xacml:AttributeValue>"
                + xmlEscape(subject)
                + "</xacml:AttributeValue></xacml:Attribute></xacml:Attributes>"
                + "<xacml:Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">"
                + "<xacml:Attribute AttributeId=\"resource.type\"><xacml:AttributeValue>"
                + xmlEscape(resource)
                + "</xacml:AttributeValue></xacml:Attribute>"
                + "<xacml:Attribute AttributeId=\"resource.id\"><xacml:AttributeValue>"
                + xmlEscape(String.valueOf(resourceId))
                + "</xacml:AttributeValue></xacml:Attribute></xacml:Attributes>"
                + "<xacml:Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\">"
                + "<xacml:Attribute AttributeId=\"action\"><xacml:AttributeValue>"
                + xmlEscape(action)
                + "</xacml:AttributeValue></xacml:Attribute></xacml:Attributes>"
                + "</xacml:Request></soapenv:Body></soapenv:Envelope>";
        try {
            return soapXacmlDecisionFromBody(
                    postPdpText(decisionUrl, payload, "text/xml"),
                    reasonField,
                    reasonRequired,
                    policyBundleId,
                    policyBundleVersion);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return deny("pdp_interrupted", policyBundleId, policyBundleVersion);
        } catch (RuntimeException | java.io.IOException ex) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
    }

    // PDP adapter protocol: custom-json-entitlement
    private PdpDecision decideCustomEntitlement(
            String decisionUrlEnv,
            String subject,
            String resource,
            Object resourceId,
            String action,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        String decisionUrl = System.getenv(decisionUrlEnv);
        if (decisionUrl == null || decisionUrl.isBlank() || resourceId == null) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
        String payload = "{\"subject\":\"" + jsonEscape(subject)
                + "\",\"resourceType\":\"" + jsonEscape(resource)
                + "\",\"resourceId\":\"" + jsonEscape(String.valueOf(resourceId))
                + "\",\"action\":\"" + jsonEscape(action)
                + "\",\"policyBundleId\":\"" + jsonEscape(policyBundleId)
                + "\"}";
        try {
            return customEntitlementDecisionFromBody(
                    postPdpJson(decisionUrl, payload),
                    reasonField,
                    reasonRequired,
                    policyBundleId,
                    policyBundleVersion);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return deny("pdp_interrupted", policyBundleId, policyBundleVersion);
        } catch (RuntimeException | java.io.IOException ex) {
            return deny("pdp_unavailable", policyBundleId, policyBundleVersion);
        }
    }

    private java.util.Map<String, Object> postPdpJson(String decisionUrl, String payload)
            throws java.io.IOException, InterruptedException {
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(decisionUrl))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofMillis(pdpTimeoutMillis()))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload))
                .build();
        java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofMillis(pdpTimeoutMillis()))
                .build()
                .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new java.io.IOException("PDP HTTP " + response.statusCode());
        }
        String body = response.body() == null ? "{}" : response.body();
        if (body.isBlank()) {
            return java.util.Map.of();
        }
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(
                        body,
                        new com.fasterxml.jackson.core.type.TypeReference<
                                java.util.Map<String, Object>>() {});
    }

    private String postPdpText(String decisionUrl, String payload, String contentType)
            throws java.io.IOException, InterruptedException {
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(decisionUrl))
                .header("Content-Type", contentType)
                .timeout(java.time.Duration.ofMillis(pdpTimeoutMillis()))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload))
                .build();
        java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofMillis(pdpTimeoutMillis()))
                .build()
                .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return "";
        }
        return response.body() == null ? "" : response.body();
    }

    private long pdpTimeoutMillis() {
        for (String envName : java.util.List.of(
                "PDP_AUTHZ_TIMEOUT_MS",
                "SOAP_XACML_TIMEOUT_MS",
                "CUSTOM_ENTITLEMENT_TIMEOUT_MS")) {
            String raw = System.getenv(envName);
            if (raw == null || raw.isBlank()) {
                continue;
            }
            try {
                return Math.max(100L, Long.parseLong(raw.trim()));
            } catch (NumberFormatException ex) {
                return 2_000L;
            }
        }
        return 2_000L;
    }

    private PdpDecision opaDecisionFromBody(
            java.util.Map<String, Object> body,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        if (body == null || body.isEmpty() || !body.containsKey("result")) {
            return deny("invalid_decision_response", policyBundleId, policyBundleVersion);
        }
        Object result = body.get("result");
        if (result instanceof Boolean allowed) {
            return new PdpDecision(
                    allowed, allowed ? "allow" : "deny", policyBundleId, policyBundleVersion);
        }
        if (result instanceof java.util.Map<?, ?> resultMap) {
            boolean allowed = Boolean.TRUE.equals(resultMap.get("allow"));
            String reason = authorizationString(resultMap.get(reasonField));
            if (reason.isBlank()) {
                reason = authorizationString(resultMap.get("reason"));
            }
            if (reasonRequired && reason.isBlank()) {
                return deny("missing_decision_reason", policyBundleId, policyBundleVersion);
            }
            if (reason.isBlank()) {
                reason = allowed ? "allow" : "deny";
            }
            return new PdpDecision(allowed, reason, policyBundleId, policyBundleVersion);
        }
        return deny("invalid_decision_response", policyBundleId, policyBundleVersion);
    }

    private PdpDecision cedarDecisionFromBody(
            java.util.Map<String, Object> body,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        String decisionValue = body == null ? "" : authorizationString(body.get("decision"));
        if (decisionValue.isBlank()) {
            return deny("invalid_decision_response", policyBundleId, policyBundleVersion);
        }
        boolean allowed = "allow".equalsIgnoreCase(decisionValue);
        String reason = authorizationString(body.get("reason"));
        Object diagnostics = body.get("diagnostics");
        if (reason.isBlank() && diagnostics instanceof java.util.Map<?, ?> diagnosticsMap) {
            reason = authorizationString(diagnosticsMap.get(reasonField));
            if (reason.isBlank()) {
                reason = authorizationString(diagnosticsMap.get("reason"));
            }
        }
        if (reasonRequired && reason.isBlank()) {
            return deny("missing_decision_reason", policyBundleId, policyBundleVersion);
        }
        if (reason.isBlank()) {
            reason = allowed ? "allow" : "deny";
        }
        return new PdpDecision(allowed, reason, policyBundleId, policyBundleVersion);
    }

    private PdpDecision soapXacmlDecisionFromBody(
            String body,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        if (body == null || body.isBlank()) {
            return deny("invalid_decision_response", policyBundleId, policyBundleVersion);
        }
        String decisionValue = xmlElementText(body, "Decision");
        if (decisionValue.isBlank()) {
            return deny("invalid_decision_response", policyBundleId, policyBundleVersion);
        }
        boolean allowed = "Permit".equalsIgnoreCase(decisionValue);
        String reason = xmlElementText(body, reasonField);
        if (reason.isBlank()) {
            reason = xmlElementText(body, "StatusMessage");
        }
        if (reason.isBlank()) {
            reason = xmlElementText(body, "Reason");
        }
        if (reasonRequired && reason.isBlank()) {
            return deny("missing_decision_reason", policyBundleId, policyBundleVersion);
        }
        if (reason.isBlank()) {
            reason = allowed ? "permit" : "deny";
        }
        return new PdpDecision(allowed, reason, policyBundleId, policyBundleVersion);
    }

    private PdpDecision customEntitlementDecisionFromBody(
            java.util.Map<String, Object> body,
            String reasonField,
            boolean reasonRequired,
            String policyBundleId,
            String policyBundleVersion) {
        if (body == null || body.isEmpty()) {
            return deny("invalid_decision_response", policyBundleId, policyBundleVersion);
        }
        Object allowValue = body.get("allow");
        String decisionValue = authorizationString(body.get("decision"));
        boolean hasDecision = allowValue instanceof Boolean || !decisionValue.isBlank();
        if (!hasDecision) {
            return deny("invalid_decision_response", policyBundleId, policyBundleVersion);
        }
        boolean allowed =
                allowValue instanceof Boolean allowedValue
                        ? allowedValue
                        : ("allow".equalsIgnoreCase(decisionValue)
                                || "permit".equalsIgnoreCase(decisionValue));
        String reason = authorizationString(body.get(reasonField));
        if (reason.isBlank()) {
            reason = authorizationString(body.get("reason"));
        }
        if (reason.isBlank()) {
            reason = authorizationString(body.get("statusMessage"));
        }
        if (reasonRequired && reason.isBlank()) {
            return deny("missing_decision_reason", policyBundleId, policyBundleVersion);
        }
        if (reason.isBlank()) {
            reason = allowed ? "allow" : "deny";
        }
        return new PdpDecision(allowed, reason, policyBundleId, policyBundleVersion);
    }

    private PdpDecision deny(
            String reason, String policyBundleId, String policyBundleVersion) {
        return new PdpDecision(false, reason, policyBundleId, policyBundleVersion);
    }

    private String jsonEscape(String value) {
        return value == null
                ? ""
                : value.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t");
    }

    private String xmlEscape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String xmlElementText(String body, String localName) {
        if (body == null || body.isBlank() || localName == null || localName.isBlank()) {
            return "";
        }
        try {
            var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            try {
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (javax.xml.parsers.ParserConfigurationException ignored) {
                // Parser support varies; namespace-aware parsing still gives a closed result below.
            }
            var document = factory.newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(new java.io.StringReader(body)));
            var nodes = document.getElementsByTagNameNS("*", localName);
            if (nodes.getLength() == 0) {
                nodes = document.getElementsByTagName(localName);
            }
            if (nodes.getLength() == 0 || nodes.item(0) == null) {
                return "";
            }
            String text = nodes.item(0).getTextContent();
            return text == null ? "" : text.trim();
        } catch (RuntimeException
                | java.io.IOException
                | javax.xml.parsers.ParserConfigurationException
                | org.xml.sax.SAXException ex) {
            return "";
        }
    }

    private record PdpDecision(
            boolean allowed, String reason, String policyBundleId, String policyBundleVersion) {}

}
