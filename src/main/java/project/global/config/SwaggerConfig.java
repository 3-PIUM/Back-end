package project.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        final String securitySchemeName = "bearerAuth";
        // 언어 전환
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT") // Optional: UI 표시용
                ))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // 전역 적용
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("PIUM API TEST")
            .description("PIUM Swagger page")
            .version("1.0.0");
    }
}