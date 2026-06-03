package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Grant a feature allowance to a plan.
 */
@Service
public class AddFeatureToPlanService {

    private final EntitlementsRepository entitlementsRepository;
    private final ApplicationEventPublisher springEventPublisher;

    public AddFeatureToPlanService(EntitlementsRepository entitlementsRepository, ApplicationEventPublisher springEventPublisher) {
        this.entitlementsRepository = entitlementsRepository;
        this.springEventPublisher = springEventPublisher;
    }

    @Transactional
    public Entitlement handle(AddFeatureToPlanRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for AddFeatureToPlan.
        // intent: Grant a feature allowance to a plan.
        // requires: EntitlementsRepository
        // emits events: EntitlementGranted
        // return: Entitlement (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.saveEntitlement.
        Entitlement value = new Entitlement(request.tenantId(), request.entitlementId(), request.planId(), request.featureKey(), request.allowance(), request.unit(), request.enforcementMode(), request.effectiveAt());
        Entitlement saved = this.entitlementsRepository.saveEntitlement(value);
        this.springEventPublisher.publishEvent(new EntitlementGranted(saved.allowance(), saved.effectiveAt(), saved.enforcementMode(), saved.entitlementId(), saved.featureKey(), saved.planId(), saved.tenantId(), saved.unit()));
        return saved;
        // === GEN_REGION_END: service-body ===
    }
}