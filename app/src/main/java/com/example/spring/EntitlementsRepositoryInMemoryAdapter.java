package com.example.spring;

import org.springframework.stereotype.Component;

/**
 * Stateful in-memory adapter for EntitlementsRepository. Spring-managed @Component
 * implementing the Port interface. Operations dispatched by name prefix:
 *   - save / update / store / add / create -- put in the entity-typed ConcurrentHashMap
 *   - find / get / lookup                  -- identity get or values stream
 *                                            (latest effectiveFrom for active versions)
 *   - list / all                           -- values stream
 *
 * A real persistence backend (sqlite, postgres, http-client) would replace this adapter.
 * The deterministic state flow it provides is enough to verify business logic at runtime.
 */
@Component
public class EntitlementsRepositoryInMemoryAdapter implements EntitlementsRepository {

    private final java.util.Map<String, AccountOverride> accountOverrideStore =
        new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, AuditEvent> auditEventStore =
        new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, Entitlement> entitlementStore =
        new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, Feature> featureStore =
        new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, Plan> planStore =
        new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, PolicyCheckResult> policyCheckResultStore =
        new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, Subscription> subscriptionStore =
        new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, UsageMeter> usageMeterStore =
        new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public Plan savePlan(Plan plan) {
        planStore.put(String.valueOf(plan.planId()), plan);
        return plan;
    }

    @Override
    public Plan findPlan(String planId) {
        return planStore.get(planId);
    }

    @Override
    public Feature saveFeature(Feature feature) {
        featureStore.put(String.valueOf(feature.featureKey()), feature);
        return feature;
    }

    @Override
    public Feature findFeature(String featureKey) {
        return featureStore.get(featureKey);
    }

    @Override
    public Entitlement saveEntitlement(Entitlement entitlement) {
        entitlementStore.put(String.valueOf(entitlement.entitlementId()), entitlement);
        return entitlement;
    }

    @Override
    public Entitlement findEntitlement(String entitlementId) {
        return entitlementStore.get(entitlementId);
    }

    @Override
    public java.util.List<Entitlement> listEntitlementsByPlan(String planId) {
        return entitlementStore.values().stream()
            .filter(value -> java.util.Objects.equals(value.planId(), planId))
            .map(value -> value)
            .toList();
    }

    @Override
    public Subscription saveSubscription(Subscription subscription) {
        subscriptionStore.put(String.valueOf(subscription.subscriptionId()), subscription);
        return subscription;
    }

    @Override
    public Subscription findSubscription(String subscriptionId) {
        return subscriptionStore.get(subscriptionId);
    }

    @Override
    public java.util.List<Subscription> listSubscriptionsByAccount(String accountId) {
        return subscriptionStore.values().stream()
            .filter(value -> java.util.Objects.equals(value.accountId(), accountId))
            .map(value -> value)
            .toList();
    }

    @Override
    public UsageMeter saveUsageMeter(UsageMeter usageMeter) {
        usageMeterStore.put(String.valueOf(usageMeter.usageId()), usageMeter);
        return usageMeter;
    }

    @Override
    public UsageMeter findUsageMeter(String usageId) {
        return usageMeterStore.get(usageId);
    }

    @Override
    public java.util.List<UsageMeter> listUsageBySubscription(String subscriptionId) {
        return usageMeterStore.values().stream()
            .filter(value -> java.util.Objects.equals(value.subscriptionId(), subscriptionId))
            .map(value -> value)
            .toList();
    }

    @Override
    public AccountOverride saveAccountOverride(AccountOverride accountOverride) {
        accountOverrideStore.put(String.valueOf(accountOverride.overrideId()), accountOverride);
        return accountOverride;
    }

    @Override
    public AccountOverride findAccountOverride(String overrideId) {
        return accountOverrideStore.get(overrideId);
    }

    @Override
    public java.util.List<AccountOverride> listOverridesByAccount(String accountId) {
        return accountOverrideStore.values().stream()
            .filter(value -> java.util.Objects.equals(value.accountId(), accountId))
            .map(value -> value)
            .toList();
    }

    @Override
    public PolicyCheckResult savePolicyCheckResult(PolicyCheckResult policyCheckResult) {
        policyCheckResultStore.put(String.valueOf(policyCheckResult.decisionId()), policyCheckResult);
        return policyCheckResult;
    }

    @Override
    public PolicyCheckResult findPolicyCheckResult(String decisionId) {
        return policyCheckResultStore.get(decisionId);
    }

    @Override
    public AuditEvent saveAuditEvent(AuditEvent auditEvent) {
        auditEventStore.put(String.valueOf(auditEvent.auditEventId()), auditEvent);
        return auditEvent;
    }

    @Override
    public java.util.List<AuditEvent> listAuditEventsByAccount(String accountId) {
        return auditEventStore.values().stream()
            .filter(value -> java.util.Objects.equals(value.accountId(), accountId))
            .map(value -> value)
            .toList();
    }

    public java.util.Collection<AccountOverride> dumpAccountOverride() {
        return accountOverrideStore.values();
    }

    public java.util.Collection<AuditEvent> dumpAuditEvent() {
        return auditEventStore.values();
    }

    public java.util.Collection<Entitlement> dumpEntitlement() {
        return entitlementStore.values();
    }

    public java.util.Collection<Feature> dumpFeature() {
        return featureStore.values();
    }

    public java.util.Collection<Plan> dumpPlan() {
        return planStore.values();
    }

    public java.util.Collection<PolicyCheckResult> dumpPolicyCheckResult() {
        return policyCheckResultStore.values();
    }

    public java.util.Collection<Subscription> dumpSubscription() {
        return subscriptionStore.values();
    }

    public java.util.Collection<UsageMeter> dumpUsageMeter() {
        return usageMeterStore.values();
    }
}
