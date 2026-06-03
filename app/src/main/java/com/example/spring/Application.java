package com.example.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


/**
 * Spring Boot entry point.
 *
 * `@EnableMethodSecurity` activates the `@PreAuthorize` annotations the controllers attach
 * to authz-protected endpoints. Without it, role guards would be silently ignored at runtime.
 */
@SpringBootApplication
@EnableMethodSecurity
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}