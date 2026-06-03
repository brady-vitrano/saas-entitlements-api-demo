package com.example.spring;

/**
 * Persistence port for plans, features, subscriptions, usage meters, overrides, policy decisions, and audit events.
 */
public interface EntitlementsRepository {

    Plan savePlan(Plan plan);

    Plan findPlan(String planId);

    Feature saveFeature(Feature feature);

    Feature findFeature(String featureKey);

    Entitlement saveEntitlement(Entitlement entitlement);

    Entitlement findEntitlement(String entitlementId);

    java.util.List<Entitlement> listEntitlementsByPlan(String planId);

    Subscription saveSubscription(Subscription subscription);

    Subscription findSubscription(String subscriptionId);

    java.util.List<Subscription> listSubscriptionsByAccount(String accountId);

    UsageMeter saveUsageMeter(UsageMeter usageMeter);

    UsageMeter findUsageMeter(String usageId);

    java.util.List<UsageMeter> listUsageBySubscription(String subscriptionId);

    AccountOverride saveAccountOverride(AccountOverride accountOverride);

    AccountOverride findAccountOverride(String overrideId);

    java.util.List<AccountOverride> listOverridesByAccount(String accountId);

    PolicyCheckResult savePolicyCheckResult(PolicyCheckResult policyCheckResult);

    PolicyCheckResult findPolicyCheckResult(String decisionId);

    AuditEvent saveAuditEvent(AuditEvent auditEvent);

    java.util.List<AuditEvent> listAuditEventsByAccount(String accountId);
}