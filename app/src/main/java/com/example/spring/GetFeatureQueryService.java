package com.example.spring;

import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Return one registered feature by feature key.
 */
@Service
public class GetFeatureQueryService {

    private final EntitlementsRepository entitlementsRepository;

    public GetFeatureQueryService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional(readOnly = true)
    public Feature handle(GetFeatureQueryRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for Query GetFeature.
        // intent: Return one registered feature by feature key.
        // reads: EntitlementsRepository.findFeature via `feature`.
        Feature row = this.entitlementsRepository.findFeature(request.featureKey());
        if (row == null) {
            throw new NotFoundException("Feature not found.");
        }
        return row;
        // === GEN_REGION_END: service-body ===
    }
}
