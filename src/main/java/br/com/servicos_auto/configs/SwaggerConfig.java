package br.com.servicos_auto.configs;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(name = "bearerAuth", // Nome do esquema de segurança
        type = SecuritySchemeType.HTTP, // Tipo de autenticação (HTTP)
        scheme = "bearer", // Esquema (Bearer token)
        bearerFormat = "JWT" // Formato do token (JWT)
)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/**")
                .build();
    }

}
