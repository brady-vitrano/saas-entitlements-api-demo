package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Record and return an entitlement authorization decision for a feature request.
 */
@Service
public class CheckFeatureAccessService {

    private final EntitlementsRepository entitlementsRepository;
    private final ApplicationEventPublisher springEventPublisher;

    public CheckFeatureAccessService(EntitlementsRepository entitlementsRepository, ApplicationEventPublisher springEventPublisher) {
        this.entitlementsRepository = entitlementsRepository;
        this.springEventPublisher = springEventPublisher;
    }

    @Transactional
    public PolicyCheckResult handle(CheckFeatureAccessRequest request) {
        // spec-driven body for CheckFeatureAccess.
        // intent: Record and return an entitlement authorization decision for a feature request.
        // requires: EntitlementsRepository
        // emits events: EntitlementChecked
        // return: PolicyCheckResult (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.savePolicyCheckResult.
        PolicyCheckResult value = new PolicyCheckResult(request.tenantId(), request.decisionId(), request.accountId(), request.subscriptionId(), request.featureKey(), request.requestedQuantity(), request.allowed(), request.reason(), request.evaluatedAt());
        PolicyCheckResult saved = this.entitlementsRepository.savePolicyCheckResult(value);
        this.springEventPublisher.publishEvent(new EntitlementChecked(saved.accountId(), saved.allowed(), saved.decisionId(), saved.evaluatedAt(), saved.featureKey(), saved.reason(), saved.requestedQuantity(), saved.subscriptionId(), saved.tenantId()));
        return saved;
    }
}