package com.mercadolivro.config

import com.mercadolivro.enums.Role
import com.mercadolivro.repository.CustomerRepository
import com.mercadolivro.security.AuthenticationFilter
import com.mercadolivro.security.AuthorizationFilter
import com.mercadolivro.security.CustomAuthenticationEntryPoint
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
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val customerRepository: CustomerRepository,
    private val userDetails: UserDetailCustomService,
    private val jwtUtil: JwtUtil,
    private val authConfiguration: AuthenticationConfiguration,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
) {

    private val PUBLIC_MATCHERS = arrayOf<String>()

    private val PUBLIC_GET_MATCHERS = arrayOf(
        "/books",
    )

    private val PUBLIC_POST_MATCHERS = arrayOf(
        "/customers",
    )

    private val ADMIN_MATCHERS = arrayOf(
        "/admins/**",
    )

    private val SWAGGER_MATCHERS = arrayOf(
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/webjars/swagger-ui/**",
    )

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    fun configureAuthenticationManager(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetails).passwordEncoder(bCryptPasswordEncoder())
    }

    @Bean
    fun configureHttpSecurity(http: HttpSecurity): SecurityFilterChain {
        http.cors().and().csrf().disable()
            .authorizeHttpRequests(
                Customizer { requests ->
                    requests
                        .requestMatchers(*PUBLIC_MATCHERS).permitAll()
                        .requestMatchers(HttpMethod.GET, *PUBLIC_GET_MATCHERS).permitAll()
                        .requestMatchers(HttpMethod.POST, *PUBLIC_POST_MATCHERS).permitAll()
                        .requestMatchers(*ADMIN_MATCHERS).hasAuthority(Role.ADMIN.description)
                        .anyRequest().authenticated()
                },
            )
            .addFilter(AuthenticationFilter(authenticationManager(authConfiguration), customerRepository, jwtUtil))
            .addFilter(AuthorizationFilter(authenticationManager(authConfiguration), userDetails, jwtUtil))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // As sessões são independentes
            .and().exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
        return http.build()
    }

    @Bean
    fun configureWebSecurity(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().requestMatchers(*SWAGGER_MATCHERS)
        }
    }

    @Bean
    fun corsConfig(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOriginPattern("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    fun authenticationManager(authConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authConfiguration.authenticationManager
    }
}
