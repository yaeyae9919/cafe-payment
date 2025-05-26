package com.cafe.payment.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("카페 주문 서비스")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("order")
                            .email("orderorder@cafe.com"),
                    ),
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("로컬 개발 서버"),
                ),
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "UserAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("x-user-id")
                            .description("사용자 ID 헤더"),
                    ),
            )
            .addSecurityItem(
                SecurityRequirement().addList("UserAuth"),
            )
    }
}
