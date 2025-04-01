package com.popeftimov.automechanic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Automechanic Documentation")
                        .version("1.0.0")
                        .description("This is the API documentation of automechanic backend")
                        .termsOfService("https://automechanic.com/terms")
                        .license(new License()
                                .name("Automechanic Terms")
                                .url("http://www.automechanic.ch/terms")
                        )
                );
    }
}
