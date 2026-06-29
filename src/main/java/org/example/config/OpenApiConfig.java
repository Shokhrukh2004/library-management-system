package org.example.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("https://library-management-system-production-b765.up.railway.app")
                        .description("Production"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local Development"));
    }
}