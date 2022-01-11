package ru.ifmo.software_engineering.afterlife.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders


private const val API_KEY = "authKey"

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI? {
        val securityRequirement = SecurityRequirement().addList(API_KEY)
        return OpenAPI()
                .components(Components()
                        .addSecuritySchemes(API_KEY, apiKeySecuritySchema())) // define the apiKey SecuritySchema
                .addSecurityItem(securityRequirement)
    }

    fun apiKeySecuritySchema(): SecurityScheme? {
        return SecurityScheme()
                .name(HttpHeaders.AUTHORIZATION)
                .description("JWT authorization token")
                .`in`(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
    }

}