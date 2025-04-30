package com.pluuginstore.brand_analytics.config;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenApiCustomizer globalResponseCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation ->
                            operation.getResponses().addApiResponse("500",
                                    new ApiResponse().description("Internal Server Error")
                                            .content(new Content().addMediaType("application/json",
                                                    new MediaType().schema(new Schema<>().$ref("#/dto/general/ErrorResponseDTO"))
                                            ))
                            )
                    )
            );
        };
    }
}
