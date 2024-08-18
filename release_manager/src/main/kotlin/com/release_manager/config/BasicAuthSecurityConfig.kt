package com.release_manager.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class BasicAuthSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            http.authorizeHttpRequests { authRequest ->
                authRequest.anyRequest().authenticated()
            }
                .httpBasic(Customizer.withDefaults())
                .sessionManagement { sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                }
                .csrf { csrfConfigurer -> csrfConfigurer.disable() }
                .headers { headers -> headers.frameOptions { frameOptions -> frameOptions.disable() } }
        }
        return http.build()
    }
}