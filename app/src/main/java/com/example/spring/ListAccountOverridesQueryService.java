package com.example.spring;

import com.example.spring.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * List account-specific entitlement overrides.
 */
@Service
public class ListAccountOverridesQueryService {

    private final EntitlementsRepository entitlementsRepository;

    public ListAccountOverridesQueryService(EntitlementsRepository entitlementsRepository) {
        this.entitlementsRepository = entitlementsRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<AccountOverride> handle(ListAccountOverridesQueryRequest request) {
        // === GEN_REGION_START: service-body ===
        // spec-driven body for Query ListAccountOverrides.
        // intent: List account-specific entitlement overrides.
        // reads: EntitlementsRepository.listOverridesByAccount via `overrides`.
        return this.entitlementsRepository.listOverridesByAccount(request.accountId());
        // === GEN_REGION_END: service-body ===
    }
}
