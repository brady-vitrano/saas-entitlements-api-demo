package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Record metered usage for a subscription feature during a billing period.
 */
@Service
public class RecordUsageService {

    private final EntitlementsRepository entitlementsRepository;
    private final ApplicationEventPublisher springEventPublisher;

    public RecordUsageService(EntitlementsRepository entitlementsRepository, ApplicationEventPublisher springEventPublisher) {
        this.entitlementsRepository = entitlementsRepository;
        this.springEventPublisher = springEventPublisher;
    }

    @Transactional
    public UsageMeter handle(RecordUsageRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for RecordUsage.
        // intent: Record metered usage for a subscription feature during a billing period.
        // requires: EntitlementsRepository
        // emits events: UsageRecorded
        // return: UsageMeter (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.saveUsageMeter.
        UsageMeter value = new UsageMeter(request.tenantId(), request.usageId(), request.subscriptionId(), request.accountId(), request.featureKey(), request.quantity(), request.periodStart(), request.periodEnd(), request.recordedAt());
        UsageMeter saved = this.entitlementsRepository.saveUsageMeter(value);
        this.springEventPublisher.publishEvent(new UsageRecorded(saved.accountId(), saved.featureKey(), saved.quantity(), saved.recordedAt(), saved.subscriptionId(), saved.tenantId(), saved.usageId()));
        return saved;
        // === GEN_REGION_END: service-body ===
    }
}