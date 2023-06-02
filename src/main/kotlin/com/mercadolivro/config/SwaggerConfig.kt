package com.mercadolivro.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!prod")
@Configuration
class SwaggerConfig {

    @Bean
    fun api(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Mercado Livro")
                .description("API do Mercado Livro")
                .version("v0.0.1"),
        )
}
