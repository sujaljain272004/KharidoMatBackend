package com.SpringProject.kharidoMat.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Bean
    public OpenAPI customOpenAPI() {
        logger.info("Initializing Swagger OpenAPI configuration");

        return new OpenAPI()
                .info(new Info()
                    .title("CampusRent — College Rental API")
                    .version("1.0.0")
                    .description("Backend APIs for booking, items, users, and payments")
                    .contact(new Contact()
                        .name("Sujal Samadiya")
                        .email("sujal@example.com")
                        .url("https://github.com/sujalsamadiya")));
    }
}
