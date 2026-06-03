package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Register a feature key that can be attached to plans and checked at runtime.
 */
@Service
public class RegisterFeatureService {

    private final EntitlementsRepository entitlementsRepository;

    public RegisterFeatureService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional
    public Feature handle(RegisterFeatureRequest request) {
        // spec-driven body for RegisterFeature.
        // intent: Register a feature key that can be attached to plans and checked at runtime.
        // requires: EntitlementsRepository
        // return: Feature (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.saveFeature.
        Feature value = new Feature(request.tenantId(), request.featureKey(), request.displayName(), request.valueType(), request.defaultUnit(), request.active());
        return this.entitlementsRepository.saveFeature(value);
    }
}