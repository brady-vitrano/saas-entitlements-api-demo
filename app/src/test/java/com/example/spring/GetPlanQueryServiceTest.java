package com.example.spring;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test scaffold for GetPlanQueryService.
 */
public class GetPlanQueryServiceTest {

    @Test
    void get_plan_service_class_loads() {
        assertNotNull(GetPlanQueryService.class, "GetPlanQueryService should be loadable");
    }
}
