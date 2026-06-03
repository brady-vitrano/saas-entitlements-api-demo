package com.example.spring;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Start a subscription for an account on a plan with a seat limit.
 */
@Service
public class StartSubscriptionService {

    private final EntitlementsRepository entitlementsRepository;
    private final ApplicationEventPublisher springEventPublisher;

    public StartSubscriptionService(EntitlementsRepository entitlementsRepository, ApplicationEventPublisher springEventPublisher) {
        this.entitlementsRepository = entitlementsRepository;
        this.springEventPublisher = springEventPublisher;
    }

    @Transactional
    public Subscription handle(StartSubscriptionRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for StartSubscription.
        // intent: Start a subscription for an account on a plan with a seat limit.
        // requires: EntitlementsRepository
        // emits events: SubscriptionStarted
        // return: Subscription (entity); spec-driven body constructs from request inputs + persists via EntitlementsRepository.saveSubscription.
        Subscription value = new Subscription(request.tenantId(), request.subscriptionId(), request.accountId(), request.planId(), request.status(), request.seatLimit(), request.currentSeats(), request.startedAt(), request.renewsAt());
        Subscription saved = this.entitlementsRepository.saveSubscription(value);
        this.springEventPublisher.publishEvent(new SubscriptionStarted(saved.accountId(), saved.planId(), saved.seatLimit(), saved.startedAt(), saved.subscriptionId(), saved.tenantId()));
        return saved;
        // === GEN_REGION_END: service-body ===
    }
}