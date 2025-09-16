package com.autoresume.autoresume_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("🧠 AutoResume.ai API Docs")
                                                .description("🚀 Empower users to generate, store, and share smart resumes using AI.")
                                                .contact(new Contact()
                                                                .name("AutoResume Support")
                                                                .email("support@autoresume.ai"))
                                                .version("v1.0.0"))
                                .components(new Components()
                                                .addSecuritySchemes("OAuth2", new SecurityScheme()
                                                                .type(SecurityScheme.Type.OAUTH2)
                                                                .description("Login with Google")
                                                                .flows(new OAuthFlows()
                                                                                .authorizationCode(new OAuthFlow()
                                                                                                .authorizationUrl(
                                                                                                                "https://accounts.google.com/o/oauth2/v2/auth")
                                                                                                .tokenUrl("https://oauth2.googleapis.com/token")
                                                                                                .scopes(new Scopes()
                                                                                                                .addString("openid",
                                                                                                                                "User info")
                                                                                                                .addString("email",
                                                                                                                                "Access user email")
                                                                                                                .addString("profile",
                                                                                                                                "Access user profile info"))))))
                                .addSecurityItem(new SecurityRequirement().addList("OAuth2"));
        }
}
