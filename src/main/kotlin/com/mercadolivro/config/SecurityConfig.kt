package com.mercadolivro.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig() {

    private val PUBLIC_MATCHERS = arrayOf<String>()

    private val PUBLIC_POST_MATCHERS = arrayOf(
        "/customers",
    )

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors().and().csrf().disable()
        http.authorizeHttpRequests(
            Customizer { requests ->
                requests
                    .requestMatchers(
                        *PUBLIC_POST_MATCHERS,
                    ).permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        *PUBLIC_POST_MATCHERS,
                    ).permitAll()
                    .anyRequest().authenticated()
            },
        )
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // As sessões são independentes
        return http.build()
    }
}
