package com.example.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test-shaped Spring Security configuration: HTTP Basic + an InMemoryUserDetailsManager
 * with legacy role-named users plus same-tenant role users for runtime proofs.
 * Every password is `secret`. This wires up the `@PreAuthorize` annotations the
 * controllers carry: requests authenticated as a role-mapped user pass through the role
 * guard; requests authenticated as a *different* role are rejected with 403.
 *
 * Production deployments would replace this with OAuth2 resource-server + real OIDC issuer wiring.
 */
@Configuration
public class SecurityConfig {

    private static final java.util.Map<String, String> SUBJECT_BY_USERNAME = java.util.Map.ofEntries(
            java.util.Map.entry("account-operator", "account-operator"),
            java.util.Map.entry("account-operator_tenant_a", "account-operator"),
            java.util.Map.entry("entitlement-admin", "entitlement-admin"),
            java.util.Map.entry("entitlement-admin_tenant_a", "entitlement-admin")
    );
    private static final java.util.Map<String, String> TENANT_BY_USERNAME = java.util.Map.ofEntries(
            java.util.Map.entry("account-operator", "account-operator"),
            java.util.Map.entry("account-operator_tenant_a", "tenant-a"),
            java.util.Map.entry("entitlement-admin", "entitlement-admin"),
            java.util.Map.entry("entitlement-admin_tenant_a", "tenant-a")
    );
    private static final java.util.Map<String, java.util.List<String>> ALLOWED_TENANTS_BY_USERNAME = java.util.Map.ofEntries(
            java.util.Map.entry("account-operator", java.util.List.of("account-operator")),
            java.util.Map.entry("account-operator_tenant_a", java.util.List.of("tenant-a")),
            java.util.Map.entry("entitlement-admin", java.util.List.of("entitlement-admin")),
            java.util.Map.entry("entitlement-admin_tenant_a", java.util.List.of("tenant-a"))
    );
    private static final java.util.Map<String, java.util.Map<String, java.util.List<String>>> CLAIMS_BY_USERNAME = java.util.Map.ofEntries(
            java.util.Map.entry("account-operator", java.util.Map.ofEntries(
            java.util.Map.entry("allowed_account_ids", java.util.List.of("account-operator"))
    )),
            java.util.Map.entry("account-operator_tenant_a", java.util.Map.ofEntries(
            java.util.Map.entry("allowed_account_ids", java.util.List.of("tenant-a"))
    )),
            java.util.Map.entry("entitlement-admin", java.util.Map.ofEntries(
            java.util.Map.entry("allowed_account_ids", java.util.List.of("entitlement-admin"))
    )),
            java.util.Map.entry("entitlement-admin_tenant_a", java.util.Map.ofEntries(
            java.util.Map.entry("allowed_account_ids", java.util.List.of("tenant-a"))
    ))
    );

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails accountOperator = User.withUsername("account-operator")
                .password(encoder.encode("secret"))
                .authorities("ROLE_account-operator", "SCOPE_entitlements:access", "SCOPE_entitlements:write")
                .build();
        UserDetails accountOperatorTenantA = User.withUsername("account-operator_tenant_a")
                .password(encoder.encode("secret"))
                .authorities("ROLE_account-operator", "SCOPE_entitlements:access", "SCOPE_entitlements:write")
                .build();
        UserDetails entitlementAdmin = User.withUsername("entitlement-admin")
                .password(encoder.encode("secret"))
                .authorities("ROLE_entitlement-admin", "SCOPE_entitlements:access", "SCOPE_entitlements:write")
                .build();
        UserDetails entitlementAdminTenantA = User.withUsername("entitlement-admin_tenant_a")
                .password(encoder.encode("secret"))
                .authorities("ROLE_entitlement-admin", "SCOPE_entitlements:access", "SCOPE_entitlements:write")
                .build();
        return new InMemoryUserDetailsManager(accountOperator, accountOperatorTenantA, entitlementAdmin, entitlementAdminTenantA);
    }

    public static String testSubjectFor(String username) {
        return SUBJECT_BY_USERNAME.getOrDefault(username, username);
    }

    public static String testTenantFor(String username) {
        return TENANT_BY_USERNAME.getOrDefault(username, username);
    }

    public static java.util.List<String> testAllowedTenantsFor(String username) {
        return ALLOWED_TENANTS_BY_USERNAME.getOrDefault(
                username, java.util.List.of(testTenantFor(username)));
    }

    public static java.util.List<String> testClaimFor(String username, String claim) {
        java.util.Map<String, java.util.List<String>> claims = CLAIMS_BY_USERNAME.get(username);
        if (claims == null) {
            return null;
        }
        return claims.get(claim);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/healthz", "/actuator/health", "/actuator/prometheus").permitAll()
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
