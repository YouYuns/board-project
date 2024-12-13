package com.shyun.boardproject.config;

/*
 * Copyright (c) 2024. Dfocus Co., Ltd . ALL RIGHT RESERVED.
 *
 * -설명 입력-
 * @author : 정은국
 * @date    : 2024.2.6
 *
 * @change history
 * ---------------------------------------------------------------------------------------------------------------------
 * - 2024.2.6 정은국 - 최초 생성
 * ---------------------------------------------------------------------------------------------------------------------
 */


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "API",
                version = "v2.0.0"
        )
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi articleApi() {
        String[] paths = {"/api/**"};

        return GroupedOpenApi.builder()
                .group("article Management")
                .pathsToMatch(paths)
                .build();
    }


}

