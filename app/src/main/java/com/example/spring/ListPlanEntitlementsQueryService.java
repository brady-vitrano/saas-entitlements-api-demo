package com.example.spring;

import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * List entitlements granted by a plan.
 */
@Service
public class ListPlanEntitlementsQueryService {

    private final EntitlementsRepository entitlementsRepository;

    public ListPlanEntitlementsQueryService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<Entitlement> handle(ListPlanEntitlementsQueryRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for Query ListPlanEntitlements.
        // intent: List entitlements granted by a plan.
        // reads: EntitlementsRepository.listEntitlementsByPlan via `entitlements`.
        return this.entitlementsRepository.listEntitlementsByPlan(request.planId());
        // === GEN_REGION_END: service-body ===
    }
}
