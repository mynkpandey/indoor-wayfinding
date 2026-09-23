package com.movieinsync.wayfinding.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI indoorWayfindingOpenAPI() {

        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Indoor Wayfinding API")
                        .version("1.0")
                        .description(
                                "REST API for indoor navigation, shortest-path routing, " +
                                "wheelchair accessibility, POI search, and multi-stop routing."
                        ))
                .components(new Components()
                        .addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Enter your Basic Authentication username and password.")
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                );
    }
}