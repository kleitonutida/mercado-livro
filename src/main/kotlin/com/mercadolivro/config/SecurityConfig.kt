package com.mercadolivro.config

import com.mercadolivro.enums.Role
import com.mercadolivro.repository.CustomerRepository
import com.mercadolivro.security.AuthenticationFilter
import com.mercadolivro.security.AuthorizationFilter
import com.mercadolivro.security.JwtUtil
import com.mercadolivro.service.UserDetailCustomService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val customerRepository: CustomerRepository,
    private val userDetails: UserDetailCustomService,
    private val jwtUtil: JwtUtil,
    private val authConfiguration: AuthenticationConfiguration,
) {

    private val PUBLIC_MATCHERS = arrayOf<String>()

    private val PUBLIC_POST_MATCHERS = arrayOf(
        "/customers",
    )

    private val ADMIN_MATCHERS = arrayOf(
        "/admins/**",
    )

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetails).passwordEncoder(bCryptPasswordEncoder())
    }

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http.cors().and().csrf().disable()
            .authorizeHttpRequests(
                Customizer { requests ->
                    requests
                        .requestMatchers(*PUBLIC_MATCHERS).permitAll()
                        .requestMatchers(HttpMethod.POST, *PUBLIC_POST_MATCHERS).permitAll()
                        .requestMatchers(*ADMIN_MATCHERS).hasAuthority(Role.ADMIN.description)
                        .anyRequest().authenticated()
                },
            )
            .addFilter(AuthenticationFilter(authenticationManager(authConfiguration), customerRepository, jwtUtil))
            .addFilter(AuthorizationFilter(authenticationManager(authConfiguration), userDetails, jwtUtil))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // As sessões são independentes
        return http.build()
    }

    @Bean
    fun authenticationManager(authConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authConfiguration.authenticationManager
    }
}
