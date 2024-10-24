package com.tradeguard.api.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("TradeGuard API Documentation")
                .description("TradeGuard API for Customer, Asset, and Transaction Management")
                .version("v1.0")
                .contact(new Contact()
                    .name("TradeGuard Support")
                    .email("support@tradeguard.com"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
            .servers(List.of(
                new Server().url("http://localhost:9090").description("Local Server")
            ))
            .externalDocs(new ExternalDocumentation()
                .description("TradeGuard Wiki Documentation")
                .url("https://tradeguard.com/docs"));
    }
}
