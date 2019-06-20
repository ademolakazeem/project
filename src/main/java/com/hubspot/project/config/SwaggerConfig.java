package com.hubspot.project.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * Sets configurations used by swagger-ui to document service apis.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Creates a docket, used by swagger to document apis.
     *
     * @return socket, used by swagger to document apis
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hubspot.project"))
                .paths(Predicates.not(PathSelectors.regex("/error")))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Hubspot REST Documentation",
                "This is a documentation that let users get information about cities.",
                "API 1.0",
                "Terms of service",
                new Contact("Ademola Kazeem",
                        "https://persistentminds.com",
                        "thepersistentminds@gmail.com"),
                "License of API", "API license URL", Collections.<VendorExtension>emptyList());
    }
}
