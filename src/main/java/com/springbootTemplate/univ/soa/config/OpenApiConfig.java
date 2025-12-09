package com.springbootTemplate.univ.soa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${USEREMAIL:recette@example.com}")
    private String userEmail;

    @Value("${USERNAME:MS-Recette Team}")
    private String userName;

    @Bean
    public OpenAPI RecetteServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservice Recette API")
                        .description("API REST pour la gestion des recettes via MS-Persistance")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name(userName)
                                .email(userEmail)));
    }
}
