package edu.guvi.dreamhome.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI dreamhomeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DreamHome Real Estate Platform API")
                        .description("API documentation for DreamHome — property listing, approval, and user management system.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Prasanth")
                                .email("prasanthprasa4@gmail.com")
                                .url("https://github.com/Prasanth"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
