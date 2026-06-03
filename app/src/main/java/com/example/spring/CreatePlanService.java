package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Create a subscription plan that can later grant feature entitlements.
 */
@Service
public class CreatePlanService {

    private final EntitlementsRepository entitlementsRepository;

    public CreatePlanService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional
    public Plan handle(CreatePlanRequest request) {
        // spec-driven body for CreatePlan.
        // intent: Create a subscription plan that can later grant feature entitlements.
        // requires: EntitlementsRepository
        // return: Plan (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.savePlan.
        Plan value = new Plan(request.tenantId(), request.planId(), request.planCode(), request.displayName(), request.billingInterval(), request.status(), request.createdAt());
        return this.entitlementsRepository.savePlan(value);
    }
}