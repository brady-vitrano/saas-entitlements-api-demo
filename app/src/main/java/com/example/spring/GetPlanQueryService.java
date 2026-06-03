package com.example.spring;

import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Return one subscription plan by id.
 */
@Service
public class GetPlanQueryService {

    private final EntitlementsRepository entitlementsRepository;

    public GetPlanQueryService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional(readOnly = true)
    public Plan handle(GetPlanQueryRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for Query GetPlan.
        // intent: Return one subscription plan by id.
        // reads: EntitlementsRepository.findPlan via `plan`.
        Plan row = this.entitlementsRepository.findPlan(request.planId());
        if (row == null) {
            throw new NotFoundException("Plan not found.");
        }
        return row;
        // === GEN_REGION_END: service-body ===
    }
}
