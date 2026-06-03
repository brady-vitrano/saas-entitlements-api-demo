package com.example.spring;

import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * List usage records for one subscription.
 */
@Service
public class ListSubscriptionUsageQueryService {

    private final EntitlementsRepository entitlementsRepository;

    public ListSubscriptionUsageQueryService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<UsageMeter> handle(ListSubscriptionUsageQueryRequest request) {
        // spec-driven body for Query ListSubscriptionUsage.
        // intent: List usage records for one subscription.
        // reads: EntitlementsRepository.listUsageBySubscription via `usageRecords`.
        return this.entitlementsRepository.listUsageBySubscription(request.subscriptionId());
    }
}
