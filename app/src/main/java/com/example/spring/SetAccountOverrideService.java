package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Create an account-specific entitlement override for support or sales operations.
 */
@Service
public class SetAccountOverrideService {

    private final EntitlementsRepository entitlementsRepository;
    private final ApplicationEventPublisher springEventPublisher;

    public SetAccountOverrideService(EntitlementsRepository entitlementsRepository, ApplicationEventPublisher springEventPublisher) {
        this.entitlementsRepository = entitlementsRepository;
        this.springEventPublisher = springEventPublisher;
    }

    @Transactional
    public AccountOverride handle(SetAccountOverrideRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for SetAccountOverride.
        // intent: Create an account-specific entitlement override for support or sales operations.
        // requires: EntitlementsRepository
        // emits events: AccountOverrideSet
        // return: AccountOverride (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.saveAccountOverride.
        AccountOverride value = new AccountOverride(request.tenantId(), request.overrideId(), request.accountId(), request.featureKey(), request.overrideType(), request.allowance(), request.reason(), request.expiresAt(), request.createdAt());
        AccountOverride saved = this.entitlementsRepository.saveAccountOverride(value);
        this.springEventPublisher.publishEvent(new AccountOverrideSet(saved.accountId(), saved.allowance(), saved.createdAt(), saved.featureKey(), saved.overrideId(), saved.overrideType(), saved.tenantId()));
        return saved;
        // === GEN_REGION_END: service-body ===
    }
}