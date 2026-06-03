package com.example.spring;

import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * List subscriptions for an account.
 */
@Service
public class ListAccountSubscriptionsQueryService {

    private final EntitlementsRepository entitlementsRepository;

    public ListAccountSubscriptionsQueryService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<Subscription> handle(ListAccountSubscriptionsQueryRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for Query ListAccountSubscriptions.
        // intent: List subscriptions for an account.
        // reads: EntitlementsRepository.listSubscriptionsByAccount via `subscriptions`.
        return this.entitlementsRepository.listSubscriptionsByAccount(request.accountId());
        // === GEN_REGION_END: service-body ===
    }
}
