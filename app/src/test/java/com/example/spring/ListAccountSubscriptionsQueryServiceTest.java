package com.example.spring;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test scaffold for ListAccountSubscriptionsQueryService.
 */
public class ListAccountSubscriptionsQueryServiceTest {

    @Test
    void list_account_subscriptions_service_class_loads() {
        assertNotNull(ListAccountSubscriptionsQueryService.class, "ListAccountSubscriptionsQueryService should be loadable");
    }
}
